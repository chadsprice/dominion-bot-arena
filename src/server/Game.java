package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Game implements Runnable {

	private static final Comparator<Card> KINGDOM_ORDER_COMPARATOR = new Comparator<Card>() {
		@Override
		public int compare(Card c1, Card c2) {
			int c1_cost = c1.cost();
			int c2_cost = c2.cost();
			if (c1_cost != c2_cost) {
				// order by highest cost
				return c2_cost - c1_cost;
			} else {
				// order alphabetically
				return c1.toString().compareTo(c2.toString());
			}
		}
	};

	private static final Comparator<Card> BASIC_ORDER_COMPARATOR = new Comparator<Card>() {
		@Override
		public int compare(Card c1, Card c2) {
			int c1_type, c2_type;
			if (c1.isVictory) {
				c1_type = 1;
			} else if (c1.isTreasure) {
				c1_type = 2;
			} else {
				c1_type = 3;
			}
			if (c2.isVictory) {
				c2_type = 1;
			} else if (c2.isTreasure) {
				c2_type = 2;
			} else {
				c2_type = 3;
			}
			if (c1_type != c2_type) {
				// victories < coins < curses 
				return c1_type - c2_type;
			} else {
				// order by cost
				return c2.cost() - c1.cost();
			}
		}
	};

	private GameServer server;
	
	private int playerIndex;
	public List<Player> players;

	public List<Card> kingdomCards;
	public List<Card> basicCards;
	public Map<Card, Integer> supply;
	public List<Card> trash;

	public boolean isGameOver;

	public int cardCostReduction;
	public int actionsPlayedThisTurn;
	public int coppersmithsPlayedThisTurn;

	public void init(GameServer server, Set<Player> playerSet, Set<Card> kingdomSet, Set<Card> basicSet) {
		this.server = server;
		kingdomCards = new ArrayList<>(kingdomSet);
		Collections.sort(kingdomCards, KINGDOM_ORDER_COMPARATOR);
		basicCards = new ArrayList<>(basicSet);
		Collections.sort(basicCards, BASIC_ORDER_COMPARATOR);
		players = new ArrayList<Player>(playerSet);
		supply = new HashMap<Card, Integer>();
		trash = new ArrayList<Card>();
	}

	@Override
	public void run() {
		setup();
		while (!gameOverConditionMet()) {
			takeTurn(players.get(playerIndex));
			playerIndex = (playerIndex + 1) % players.size();
		}
		announceWinner();
		endGame();
	}

	public void setup() {
		// kingdom cards
		for (Card card : kingdomCards) {
			supply.put(card, card.startingSupply(players.size()));
		}
		// basic cards
		for (Card card : basicCards) {
			supply.put(card, card.startingSupply(players.size()));
		}
		// randomize turn order
		Collections.shuffle(players);
		for (Player player : players) {
			setKingdomCards(player);
			setBasicCards(player);
			setPileSizes(player, supply);
			player.startGame();
			clearActions(player);
			clearBuys(player);
		}
	}

	private void announceWinner() {
		boolean tieBroken = false;
		Map<Player, VictoryReportCard> reportCards = new HashMap<Player, VictoryReportCard>();
		for (Player player : players) {
			reportCards.put(player, new VictoryReportCard(player));
		}
		int winningPoints = reportCards.get(players.get(0)).points;
		int winningTurns = players.get(0).turns;
		boolean winnerForfeited = players.get(0).forfeit;
		for (int i = 1; i < players.size(); i++) {
			if (players.get(i).forfeit) {
				continue;
			}
			int points = reportCards.get(players.get(i)).points;
			int turns = players.get(i).turns;
			// winner decided by more points
			if (points > winningPoints || winnerForfeited) {
				winningPoints = points;
				winningTurns = turns;
				winnerForfeited = players.get(i).forfeit;
				tieBroken = false;
			} else if (points == winningPoints) {
				// winner decided by fewer turns on a tie
				if (turns < winningTurns) {
					winningTurns = turns;
					tieBroken = true;
				}
			}
		}
		for (Player player : players) {
			message(player, "The game has ended");
			message(player, "You had " + reportCards.get(player).points + " victory points (" + Card.htmlList(reportCards.get(player).victoryCards) + ")");
			for (Player opponent : getOpponents(player)) {
				if (opponent.forfeit) {
					message(player, opponent.username + " forfeited, having " + reportCards.get(opponent).points + " victory points (" + Card.htmlList(reportCards.get(opponent).victoryCards) + ")");
				} else {
					message(player, opponent.username + " had " + reportCards.get(opponent).points + " victory points (" + Card.htmlList(reportCards.get(opponent).victoryCards) + ")");
				}
			}
			// if this player won
			if (reportCards.get(player).points == winningPoints && player.turns == winningTurns) {
				message(player, "You win!");
			}
			// otherwise, announce all winning opponents
			for (Player opponent : getOpponents(player)) {
				if (reportCards.get(opponent).points == winningPoints && opponent.turns == winningTurns && !opponent.forfeit) {
					message(player, opponent.username + " wins");
				}
			}
		}
		if (tieBroken) {
			for (Player player : players) {
				message(player, "(Tie broken by fewest turns taken)");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void endGame() {
		isGameOver = true;
		JSONObject command = new JSONObject();
		command.put("command", "endGame");
		for (Player player : players) {
			player.sendCommand(command);
		}
		issueCommandsToAllPlayers();
	}

	private static class VictoryReportCard {
		public int points;
		public List<Card> victoryCards;

		public VictoryReportCard(Player player) {
			points = 0;
			List<Card> deck = new ArrayList<Card>();
			deck.addAll(player.getDraw());
			deck.addAll(player.getHand());
			deck.addAll(player.getPlay());
			deck.addAll(player.getDiscard());
			victoryCards = new ArrayList<Card>();
			for (Card card : deck) {
				if (card.isVictory || card == Card.CURSE) {
					points += card.victoryValue(deck);
					victoryCards.add(card);
				}
			}
		}
	}

	private void takeTurn(Player player) {
		if (cardCostReduction > 0) {
			setCardCostReduction(0);
		}
		actionsPlayedThisTurn = 0;
		message(player, "------ Your turn ------");
		messageOpponents(player, "------ " + player.username + "'s turn ------");
		player.sendActions();
		player.sendBuys();
		while (player.getActions() > 0 && player.hasPlayableAction()) {
			// action phase
			Set<Card> choices = playableActions(player);
			Card choice = promptChooseFromHand(player, choices, "Action Phase: Choose an action to play", false, "No Action");
			if (choice == null) {
				break;
			}
			// put action card in play
			player.putFromHandIntoPlay(choice);
			player.addActions(-1);
			// play action
			playAction(player, choice);
		}
		clearActions(player);
		boolean givenBuyPrompt = false;
		while (player.getBuys() > 0 && canBuyCard(player)) {
			// buy phase
			Set<Card> choices = buyableCards(player);
			givenBuyPrompt = true;
			Card choice = promptChooseFromSupply(player, choices, "Buy Phase: Choose a card to purchase", "buyPrompt", false, "End Turn");
			if (choice == null) {
				break;
			}
			// gain purchased card
			gain(player, choice);
			message(player, "You purchase " + choice.htmlName());
			messageOpponents(player, player.username + " purchases " + choice.htmlName());
			// update player status
			player.addBuys(-1);
			player.addExtraCoins(-choice.cost(this));
		}
		clearBuys(player);
		coppersmithsPlayedThisTurn = 0;
		// if the player couldn't buy anything, notify them that their turn is over
		if (!givenBuyPrompt) {
			promptMultipleChoice(player, "There are no cards that you can buy this turn", new String[] {"End Turn"});
		}
		// cleanup and redraw
		player.cleanup();
		for (Player eachPlayer : players) {
			message(eachPlayer, "...");
		}
		player.turns++;
	}

	public void playAction(Player player, Card action) {
		actionsPlayedThisTurn++;
		message(player, "You play " + action.htmlName());
		messageOpponents(player, player.username + " plays " + action.htmlName());
		if (action.isAttack) {
			// attack reactions
			List<Player> targets = new ArrayList<Player>();
			for (Player opponent : getOpponents(player)) {
				boolean unaffected = reactToAttack(opponent);
				if (!unaffected) {
					targets.add(opponent);
				}
			}
			action.onAttack(player, this, targets);
		} else {
			action.onPlay(player, this);
		}
	}

	public List<Player> getOpponents(Player player) {
		List<Player> opponents = new ArrayList<Player>();
		for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
			if (players.get(playerIndex) == player) {
				int i = (playerIndex + 1) % players.size();
				while (i != playerIndex) {
					opponents.add(players.get(i));
					i = (i + 1) % players.size();
				}
				return opponents;
			}
		}
		throw new IllegalArgumentException();
	}

	private boolean reactToAttack(Player player) {
		boolean unaffected = false;
		Set<Card> reactions = getAttackReactions(player);
		if (reactions.size() > 0) {
			do {
				Card choice = promptChooseFromHand(player, reactions, "Choose a reaction", "reactionPrompt", false, "No Reaction");
				if (choice != null) {
					message(player, ".. (You reveal " + choice.htmlName() + ")");
					messageOpponents(player, "... (" + player.username + " reveals " + choice.htmlName() + ")");
					unaffected |= choice.onAttackReaction(player, this);
					// update possible reactions
					reactions = getAttackReactions(player);
					// don't allow the same reaction to be played twice in a row
					// (they are designed so that playing them twice in a row gives no new benefit)
					reactions.remove(choice);
				} else {
					break;
				}
			} while (reactions.size() > 0);
		}
		return unaffected;
	}

	private Set<Card> getAttackReactions(Player player) {
		Set<Card> reactions = new HashSet<>();
		for (Card card : player.getHand()) {
			if (card.isAttackReaction) {
				reactions.add(card);
			}
		}
		return reactions;
	}

	public void takeFromSupply(Card card) {
		// update supply
		supply.put(card, supply.get(card) - 1);
		Map<Card, Integer> newSize = new HashMap<Card, Integer>();
		newSize.put(card, supply.get(card));
		for (Player eachPlayer : players) {
			setPileSizes(eachPlayer, newSize);
		}
	}

	public void gain(Player player, Card card) {
		takeFromSupply(card);
		// put card in player's discard
		player.addToDiscard(card);
	}

	private boolean canBuyCard(Player player) {
		int coins = player.getCoins();
		for (Map.Entry<Card, Integer> pile : supply.entrySet()) {
			Card card = pile.getKey();
			Integer count = pile.getValue();
			if (card.cost(this) <= coins && count > 0) {
				return true;
			}
		}
		return false;
	}

	private Set<Card> buyableCards(Player player) {
		Set<Card> cards = new HashSet<Card>();
		int coins = player.getCoins();
		for (Map.Entry<Card, Integer> pile : supply.entrySet()) {
			Card card = pile.getKey();
			Integer count = pile.getValue();
			if (card.cost(this) <= coins && count > 0) {
				cards.add(card);
			}
		}
		return cards;
	}

	public Set<Card> cardsCostingExactly(int cost) {
		Set<Card> cards = new HashSet<Card>();
		for (Map.Entry<Card, Integer> pile : supply.entrySet()) {
			Card card = pile.getKey();
			Integer count = pile.getValue();
			if (card.cost(this) == cost && count > 0) {
				cards.add(card);
			}
		}
		return cards;
	}

	public Set<Card> cardsCostingAtMost(int cost) {
		Set<Card> cards = new HashSet<Card>();
		for (Map.Entry<Card, Integer> pile : supply.entrySet()) {
			Card card = pile.getKey();
			Integer count = pile.getValue();
			if (card.cost(this) <= cost && count > 0) {
				cards.add(card);
			}
		}
		return cards;
	}

	public Set<Card> playableActions(Player player) {
		Set<Card> actions = new HashSet<Card>();
		for (Card card : player.getHand()) {
			if (card.isAction) {
				actions.add(card);
			}
		}
		return actions;
	}

	private boolean gameOverConditionMet() {
		// check if game has been forfeited
		if (isGameOver) {
			return true;
		}
		// check if province pile is empty
		if (supply.get(Card.PROVINCE) == 0) {
			return true;
		}
		// check if three supply piles are empty
		int emptyPiles = 0;
		for (Integer count : supply.values()) {
			if (count == 0) {
				emptyPiles++;
			}
		}
		return emptyPiles >= 3;
	}

	public void addCardCostReduction(int toAdd) {
		setCardCostReduction(cardCostReduction + toAdd);
	}

	public void setCardCostReduction(int reduction) {
		cardCostReduction = reduction;
		for (Player player : players) {
			setCardCosts(player);
		}
	}

	@SuppressWarnings("unchecked")
	public void setCardCosts(Player player) {
		JSONObject command = new JSONObject();
		command.put("command", "setCardCosts");
		JSONObject costs = new JSONObject();
		for (Card card : supply.keySet()) {
			costs.put(card.toString(), card.cost(this));
		}
		command.put("costs", costs);
		player.sendCommand(command);
	}

	@SuppressWarnings("unchecked")
	public void setKingdomCards(Player player) {
		JSONObject setKingdomCards = new JSONObject();
		setKingdomCards.put("command", "setKingdomCards");
		JSONArray cards = new JSONArray();
		for (Card kingdomCard : kingdomCards) {
			JSONObject card = new JSONObject();
			card.put("name", kingdomCard.toString());
			card.put("cost", kingdomCard.cost());
			card.put("className", kingdomCard.htmlClass());
			card.put("type", kingdomCard.htmlType());
			JSONArray description = new JSONArray();
			for (String line : kingdomCard.description()) {
				description.add(line);
			}
			card.put("description", description);
			cards.add(card);
		}
		setKingdomCards.put("cards", cards);

		player.sendCommand(setKingdomCards);
	}

	@SuppressWarnings("unchecked")
	public void setBasicCards(Player player) {
		JSONObject setBasicCards = new JSONObject();
		setBasicCards.put("command", "setBasicCards");
		JSONArray cards = new JSONArray();
		for (Card basicCard : basicCards) {
			JSONObject card = new JSONObject();
			card.put("name", basicCard.toString());
			card.put("cost", basicCard.cost());
			card.put("className", basicCard.htmlClass());
			card.put("type", basicCard.htmlType());
			JSONArray description = new JSONArray();
			for (String line : basicCard.description()) {
				description.add(line);
			}
			card.put("description", description);
			cards.add(card);
		}
		setBasicCards.put("cards", cards);

		player.sendCommand(setBasicCards);
	}

	@SuppressWarnings("unchecked")
	public void setPileSizes(Player player, Map<Card, Integer> sizes) {
		JSONObject setPileSizes = new JSONObject();
		setPileSizes.put("command", "setPileSizes");
		JSONObject piles = new JSONObject();
		for (Map.Entry<Card, Integer> size : sizes.entrySet()) {
			piles.put(size.getKey(), size.getValue());
		}
		setPileSizes.put("piles", piles);

		player.sendCommand(setPileSizes);
	}

	@SuppressWarnings("unchecked")
	public void clearActions(Player player) {
		JSONObject setActions = new JSONObject();
		setActions.put("command", "setActions");
		setActions.put("actions", "");
		player.sendCommand(setActions);
	}

	@SuppressWarnings("unchecked")
	public void clearBuys(Player player) {
		JSONObject setBuys = new JSONObject();
		setBuys.put("command", "setBuys");
		setBuys.put("buys", "");
		player.sendCommand(setBuys);
	}

	public void forfeit(Player player, boolean connectionClosed) {
		if (isGameOver) {
			// ignore forfeits if the game is already over
			return;
		}
		player.forfeit = true;
		player.responses.add("forfeit");
		if (connectionClosed) {
			messageOpponents(player, "<span class=\"forfeit\">" + player.username + " has been disconnected, forfeiting the game!</span>");
		} else {
			messageOpponents(player, "<span class=\"forfeit\">" + player.username + " forfeits!</span>");
		}
		issueCommandsToAllPlayers();
		int activePlayers = 0;
		for (Player eachPlayer : players) {
			if (!eachPlayer.forfeit) {
				activePlayers++;
			}
		}
		if (activePlayers < 2) {
			isGameOver = true;
		}
	}

	@SuppressWarnings("unchecked")
	private void waitOn(Player player) {
		for (Player opponent : getOpponents(player)) {
			JSONObject command = new JSONObject();
			command.put("command", "setWaitingOn");
			command.put("player", player.username);
			opponent.sendCommand(command);
		}
		issueCommandsToAllPlayers();
	}

	private void issueCommandsToAllPlayers() {
		for (Player player : players) {
			player.issueCommands();
		}
	}

	private static final int SECONDS_BEFORE_HURRY_UP = 20;
	private static final int SECONDS_AFTER_HURRY_UP = 40;
	private Boolean canHurryUp = false;
	private Player toHurryUp;
	private boolean hurryingUp;
	private long hurryUpStartTime;
	@SuppressWarnings("unchecked")
	private Object waitForResponse(Player player) {
		canHurryUp = false;
		Object response = null;
		if (!player.forfeit) {
			// announce which player we are waiting on
			waitOn(player);
			// wait until response or "hurry up" becomes available
			try {
				response = player.responses.poll(SECONDS_BEFORE_HURRY_UP, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// if "hurry up" timeout was reached
			if (response == null && !player.forfeit) {
				// allow opponents to "hurry up" this player
				canHurryUp = true;
				toHurryUp = player;
				JSONObject command = new JSONObject();
				command.put("command", "allowHurryUp");
				for (Player opponent : getOpponents(player)) {
					opponent.issueCommand(command);
				}
				// continue to wait
				while (response == null && !player.forfeit) {
					try {
						response = player.responses.poll(5, TimeUnit.SECONDS);
						// if another player asked this one to hurry up and enough time has passed
						if (response == null && hurryingUp && (System.currentTimeMillis() - hurryUpStartTime > SECONDS_AFTER_HURRY_UP * 1000)) {
							// this player automatically forfeits
							server.forfeit(player);
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				hurryingUp = false;
			}
		}
		canHurryUp = false;
		return response;
	}

	public void hurryUp(Player sender) {
		if (canHurryUp && !hurryingUp) {
			hurryingUp = true;
			hurryUpStartTime = System.currentTimeMillis();
			for (Player player : players) {
				if (player == toHurryUp) {
					message(player, "<span class=\"hurryUp\">" + sender.username + " asks you to hurry up! You will automatically forfeit in " + SECONDS_AFTER_HURRY_UP + " seconds!</span>");
				} else if (player == sender) {
					message(player, "<span class=\"hurryUp\">" + "You ask " + toHurryUp.username + " to hurry up! He will automatically forfeit in " + SECONDS_AFTER_HURRY_UP + " seconds!</span>");
				} else {
					message(player, "<span class=\"hurryUp\">" + sender.username + " asks " + toHurryUp.username + " to hurry up! He will automatically forfeit in " + SECONDS_AFTER_HURRY_UP + " seconds!</span>");
				}
			}
			issueCommandsToAllPlayers();
		}
	}

	public Card promptChooseFromSupply(Player player, Set<Card> choiceSet, String promptMessage) {
		return promptChooseFromSupply(player, choiceSet, promptMessage, true, "");
	}

	public Card promptChooseFromSupply(Player player, Set<Card> choiceSet, String promptMessage, boolean isMandatory, String noneMessage) {
		return promptChooseFromSupply(player, choiceSet, promptMessage, "actionPrompt", isMandatory, noneMessage);
	}

	@SuppressWarnings("unchecked")
	public Card promptChooseFromSupply(Player player, Set<Card> choiceSet, String promptMessage, String promptType, boolean isMandatory, String noneMessage) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card response = bot.chooseFromSupply(choiceSet);
			if (!choiceSet.contains(response)) {
				throw new IllegalStateException();
			}
			return response;
		}
		// construct JSON message
		JSONObject prompt = new JSONObject();
		prompt.put("command", "promptChooseFromSupply");
		JSONArray choiceArray = new JSONArray();
		for (Card card : choiceSet) {
			choiceArray.add(card.toString());
		}
		prompt.put("choices", choiceArray);
		prompt.put("message", promptMessage);
		prompt.put("promptType", promptType);
		prompt.put("isMandatory", isMandatory);
		prompt.put("noneMessage", noneMessage);
		player.sendCommand(prompt);
		// wait for response
		String response = null;
		try {
			response = (String) waitForResponse(player);
		} catch (ClassCastException e) {
			// ignore incorrect responses
		}
		// parse response
		Card chosen = Card.fromName(response);
		// verify response
		if (choiceSet.contains(chosen)) {
			return chosen;
		} else {
			if (isMandatory) {
				return choiceSet.iterator().next();
			} else {
				return null;
			}
		}
	}

	public Card promptChooseFromHand(Player player, Set<Card> choiceSet, String promptMessage) {
		return promptChooseFromHand(player, choiceSet, promptMessage, "actionPrompt", true, "");
	}

	public Card promptChooseFromHand(Player player, Set<Card> choiceSet, String promptMessage, String promptType) {
		return promptChooseFromHand(player, choiceSet, promptMessage, promptType, true, "");
	}

	public Card promptChooseFromHand(Player player, Set<Card> choiceSet, String promptMessage, boolean isMandatory, String noneMessage) {
		return promptChooseFromHand(player, choiceSet, promptMessage, "actionPrompt", isMandatory, noneMessage);
	}

	@SuppressWarnings("unchecked")
	public Card promptChooseFromHand(Player player, Set<Card> choiceSet, String promptMessage, String promptType, boolean isMandatory, String noneMessage) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card response = bot.chooseFromHand(choiceSet);
			if (!choiceSet.contains(response)) {
				throw new IllegalStateException();
			}
			return response;
		}
		// construct JSON message
		JSONObject prompt = new JSONObject();
		prompt.put("command", "promptChooseFromHand");
		JSONArray choiceArray = new JSONArray();
		for (Card card : choiceSet) {
			choiceArray.add(card.toString());
		}
		prompt.put("choices", choiceArray);
		prompt.put("message", promptMessage);
		prompt.put("promptType", promptType);
		prompt.put("isMandatory", isMandatory);
		prompt.put("noneMessage", noneMessage);
		player.sendCommand(prompt);
		// wait for response
		String response = null;
		try {
			response = (String) waitForResponse(player);
		} catch (ClassCastException e) {
			// ignore incorrect responses
		}
		// parse response
		Card chosen = Card.fromName(response);
		// verify response
		if (choiceSet.contains(chosen)) {
			return chosen;
		} else {
			return null;
		}
	}

	public List<Card> discardNumber(Player player, int number, String cause, String promptType) {
		// if the player has that many cards or fewer
		if (player.getHand().size() <= number) {
			// discard entire hand
			return player.getHand();
		} else {
			// otherwise prompt player
			return promptDiscardNumber(player, number, true, cause, promptType, "discard");
		}
	}

	public List<Card> promptDiscardNumber(Player player, int number, boolean isMandatory, String cause, String destination) {
		return promptDiscardNumber(player, number, isMandatory, cause, "actionPrompt", destination);
	}

	@SuppressWarnings("unchecked")
	public List<Card> promptDiscardNumber(Player player, int number, boolean isMandatory, String cause, String promptType, String destination) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			List<Card> response = bot.discardNumber(number);
			List<Card> handCopy = new ArrayList<Card>(bot.getHand());
			for (Card card : response) {
				if (!handCopy.remove(card)) {
					throw new IllegalStateException();
				}
			}
			return response;
		}
		// prompt
		JSONObject prompt = new JSONObject();
		prompt.put("command", "promptDiscardNumber");
		prompt.put("number", number);
		prompt.put("isMandatory", isMandatory);
		prompt.put("cause", cause);
		prompt.put("promptType", promptType);
		prompt.put("destination", destination);
		player.sendCommand(prompt);
		// wait for response
		JSONArray response = null;
		try {
			response = (JSONArray) waitForResponse(player);
		} catch (ClassCastException e) {
			// ignore incorrect responses
		}
		// parse response
		boolean valid = true;
		List<Card> discarded = null;
		if (response != null) {
			discarded = parseJsonCardList(response);
		}
		// verify response
		if (discarded == null || (isMandatory && discarded.size() != number)) {
			valid = false;
		} else {
			// make sure hand has all chosen cards
			List<Card> handCopy = new ArrayList<>(player.getHand());
			for (Card card : discarded) {
				if (!handCopy.remove(card)) {
					valid = false;
					break;
				}
			}
		}
		if (valid) {
			return discarded;
		} else {
			if (isMandatory) {
				// if the response is invalid, just discard the first few in hand
				return new ArrayList<Card>(player.getHand().subList(0, number));
			} else {
				return new ArrayList<Card>();
			}
		}
	}

	public int promptMultipleChoice(Player player, String promptMessage, String[] choices) {
		return promptMultipleChoice(player, promptMessage, choices, null);
	}

	public int promptMultipleChoice(Player player, String promptMessage, String[] choices, int[] disabledIndexes) {
		return promptMultipleChoice(player, promptMessage, "actionPrompt", choices, disabledIndexes);
	}

	public int promptMultipleChoice(Player player, String promptMessage, String promptType, String[] choices) {
		return promptMultipleChoice(player, promptMessage, promptType, choices, null);
	}

	@SuppressWarnings("unchecked")
	public int promptMultipleChoice(Player player, String promptMessage, String promptType, String[] choices, int[] disabledIndexes) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			int response = bot.multipleChoice(choices, disabledIndexes);
			if (response < 0 || response >= choices.length) {
				throw new IllegalStateException();
			}
			if (disabledIndexes != null) {
				for (int i = 0; i < disabledIndexes.length; i++) {
					if (response == disabledIndexes[i]) {
						throw new IllegalStateException();
					}
				}
			}
			return response;
		}
		JSONObject prompt = new JSONObject();
		prompt.put("command", "promptMultipleChoice");
		JSONArray choiceArray = new JSONArray();
		for (String choice : choices) {
			choiceArray.add(choice);
		}
		prompt.put("choices", choiceArray);
		if (disabledIndexes != null) {
			JSONArray disabledIndexArray = new JSONArray();
			for (Integer disabledIndex : disabledIndexes) {
				disabledIndexArray.add(disabledIndex);
			}
			prompt.put("disabled", disabledIndexArray);
		}
		prompt.put("message", promptMessage);
		prompt.put("promptType", promptType);
		player.sendCommand(prompt);
		String response = null;
		try {
			response = (String) waitForResponse(player);
		} catch (ClassCastException e) {
			// ignore incorrect responses
		}
		int choiceIndex = 0;
		try {
			choiceIndex = Integer.parseInt(response);
		} catch (NumberFormatException e) {
			// default to zero
		}
		if (choiceIndex < 0 || choiceIndex >= choices.length) {
			choiceIndex = 0;
		}
		if (disabledIndexes != null) {
			while (isDisabledChoice(choiceIndex, disabledIndexes)) {
				choiceIndex = (choiceIndex + 1) % choices.length;
			}
		}
		return choiceIndex;
	}

	private boolean isDisabledChoice(int choiceIndex, int[] disabledIndexes) {
		for (int i = 0; i < disabledIndexes.length; i++) {
			if (choiceIndex == disabledIndexes[i]) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void message(Player player, String str) {
		JSONObject message = new JSONObject();
		message.put("command", "message");
		message.put("message", str);
		player.sendCommand(message);
	}

	public void messageOpponents(Player player, String str) {
		for (Player opponent : getOpponents(player)) {
			message(opponent, str);
		}
	}

	private List<Card> parseJsonCardList(JSONArray array) {
		List<Card> list = new ArrayList<Card>();
		try {
			for (Object object : array) {
				String cardName = (String) object;
				Card card = Card.fromName(cardName);
				if (card == null) {
					throw new IllegalArgumentException();
				}
				list.add(card);
			}
		} catch (Exception e) {

		}
		return list;
	}

}
