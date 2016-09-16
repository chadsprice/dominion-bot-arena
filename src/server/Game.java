package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

	// various bits of game state required by individual card rules
	public int cardCostReduction;
	public int actionsPlayedThisTurn;
	public int coppersmithsPlayedThisTurn;
	public Map<Card, Integer> embargoTokens;
	public boolean boughtVictoryCardThisTurn;

	public int messageIndent;

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
			if (!players.get(playerIndex).hasExtraTurn()) {
				playerIndex = (playerIndex + 1) % players.size();
			}
		}
		announceWinner();
		endGame();
	}

	public void setup() {
		// add kingdom cards to supply
		for (Card card : kingdomCards) {
			supply.put(card, card.startingSupply(players.size()));
		}
		// add basic cards to supply
		for (Card card : basicCards) {
			supply.put(card, card.startingSupply(players.size()));
		}
		// initialize embargo tokens
		embargoTokens = new HashMap<Card, Integer>();
		for (Card cardInSupply : supply.keySet()) {
			embargoTokens.put(cardInSupply, 0);
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
		// start recording gain strategies
		initRecords();
	}

	private void takeTurn(Player player) {
		player.startNewTurn();
		if (cardCostReduction > 0) {
			setCardCostReduction(0);
		}
		actionsPlayedThisTurn = 0;
		boughtVictoryCardThisTurn = false;
		newTurnMessage(player);
		messageIndent++;
		player.sendActions();
		player.sendBuys();
		// resolve durations
		resolveDurations(player);
		while (player.getActions() > 0 && player.hasPlayableAction()) {
			// action phase
			Set<Card> choices = playableActions(player);
			Card choice = promptChoosePlay(player, choices, "Action Phase: Choose an action to play", false, "No Action");
			if (choice == null) {
				break;
			}
			// put action card in play
			player.putFromHandIntoPlay(choice);
			player.addActions(-1);
			// play action
			playAction(player, choice, false);
		}
		clearActions(player);
		boolean givenBuyPrompt = false;
		while (player.getBuys() > 0 && canBuyCard(player)) {
			// buy phase
			Set<Card> choices = buyableCards(player);
			givenBuyPrompt = true;
			Card choice = promptChooseBuy(player, choices);
			if (choice == null) {
				break;
			}
			// gain purchased card
			gain(player, choice);
			message(player, "You purchase " + choice.htmlName());
			messageOpponents(player, player.username + " purchases " + choice.htmlName());
			onBuy(player, choice);
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
		// handle treasuries
		if (!boughtVictoryCardThisTurn) {
			int numTreasuries = 0;
			for (Card card : player.getPlay()) {
				if (card == Card.TREASURY) {
					numTreasuries++;
				}
			}
			if (numTreasuries > 0) {
				String[] choices = new String[numTreasuries + 1];
				for (int i = 0; i <= numTreasuries; i++) {
					choices[i] = (numTreasuries - i) + "";
				}
				int choice = promptMultipleChoice(player, "Clean Up: Put how many Treasuries on top of your deck?", choices);
				int numToPutOnDeck = numTreasuries - choice;
				if (numToPutOnDeck > 0) {
					for (Iterator<Card> iter = player.getPlay().iterator(); numToPutOnDeck > 0 && iter.hasNext(); ) {
						if (iter.next() == Card.TREASURY) {
							iter.remove();
							player.putOnDraw(Card.TREASURY);
							numToPutOnDeck--;
						}
					}
					message(player, "You put " + Card.TREASURY.htmlName(numTreasuries - choice) + " on top of your deck");
					messageOpponents(player, player.username + " puts " + Card.TREASURY.htmlName(numTreasuries - choice) + " on top of his deck");
				}
			}
		}
		// cleanup and redraw
		player.cleanup();
		player.turns++;
		messageIndent--;
	}

	private void resolveDurations(Player player) {
		for (Duration duration : player.getDurations()) {
			// if the duration is modified (except for havens, outposts, and tacticians)
			if (duration.modifier == Card.THRONE_ROOM && duration.durationCard != Card.HAVEN && duration.durationCard != Card.OUTPOST && duration.durationCard != Card.TACTICIAN) {
				message(player, "Your " + duration.durationCard.htmlNameRaw() + " takes effect twice");
				messageOpponents(player, player.username + "'s " + duration.durationCard.htmlNameRaw() + " takes effect twice");
				messageIndent++;
				duration.durationCard.onDurationEffect(player, this, duration);
				duration.durationCard.onDurationEffect(player, this, duration);
				messageIndent--;
			} else {
				message(player, "Your " + duration.durationCard.htmlNameRaw() + " takes effect");
				messageOpponents(player, player.username + "'s " + duration.durationCard.htmlNameRaw() + " takes effect");
				messageIndent++;
				duration.durationCard.onDurationEffect(player, this, duration);
				messageIndent--;
			}
		}
		player.durationsResolved();
	}

	private void onBuy(Player player, Card card) {
		if (card.isVictory) {
			boughtVictoryCardThisTurn = true;
		}
		// if that card's pile was embargoed
		if (embargoTokens.get(card) > 0 && supply.get(Card.CURSE) > 0) {
			int cursesToGain = Math.min(embargoTokens.get(card), supply.get(Card.CURSE));
			for (int i = 0; i < cursesToGain; i++) {
				gain(player, Card.CURSE);
			}
			messageIndent++;
			messageAll("gaining " + Card.CURSE.htmlName(cursesToGain));
			messageIndent--;
		}
	}

	public boolean playAction(Player player, Card action, boolean hasMoved) {
		boolean moves = false;
		actionsPlayedThisTurn++;
		message(player, "You play " + action.htmlName());
		messageOpponents(player, player.username + " plays " + action.htmlName());
		messageIndent++;
		if (!action.isDuration) {
			if (action.isAttack) {
				// attack reactions
				List<Player> targets = new ArrayList<Player>();
				for (Player opponent : getOpponents(player)) {
					messageIndent++;
					boolean unaffected = reactToAttack(opponent);
					messageIndent--;
					if (!unaffected) {
						targets.add(opponent);
					}
				}
				action.onAttack(player, this, targets);
			} else {
				moves = action.onPlay(player, this, hasMoved);
			}
		} else {
			List<Card> toHaven = null;
			if (!hasMoved) {
				if (action == Card.HAVEN) {
					toHaven = new ArrayList<Card>();
				}
				boolean willHaveEffect = action.onDurationPlay(player, this, toHaven);
				if (willHaveEffect) {
					// take this action out of normal play and save it as a duration effect
					player.removeFromPlay(action);
					Duration duration = new Duration();
					duration.durationCard = action;
					duration.havenedCards = toHaven;
					player.addDuration(duration);
				}
				moves = willHaveEffect;
			} else {
				if (action == Card.HAVEN) {
					toHaven = player.getLastHaven();
				}
				action.onDurationPlay(player, this, toHaven);
				if (action == Card.HAVEN) {
					player.sendDurations();
				}
			}
		}
		messageIndent--;
		return moves;
	}

	private boolean reactToAttack(Player player) {
		for (Duration duration : player.getDurations()) {
			if (duration.durationCard == Card.LIGHTHOUSE) {
				message(player, "You have " + Card.LIGHTHOUSE.htmlName() + " in play");
				messageOpponents(player, player.username + " has " + Card.LIGHTHOUSE.htmlName() + " in play");
				return true;
			}
		}
		boolean unaffected = false;
		Set<Card> reactions = getAttackReactions(player);
		if (reactions.size() > 0) {
			do {
				Card choice = promptChooseRevealAttackReaction(player, reactions);
				if (choice != null) {
					message(player, "You reveal " + choice.htmlName());
					messageOpponents(player, player.username + " reveals " + choice.htmlName());
					messageIndent++;
					unaffected |= choice.onAttackReaction(player, this);
					messageIndent--;
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
				recordPlayerWin(player);
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
			List<Card> deck = player.getDeck();
			victoryCards = new ArrayList<Card>();
			for (Card card : deck) {
				if (card.isVictory || card == Card.CURSE) {
					points += card.victoryValue(deck);
					victoryCards.add(card);
				}
			}
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

	public void takeFromSupply(Card card) {
		// update supply
		supply.put(card, supply.get(card) - 1);
		Map<Card, Integer> newSize = new HashMap<Card, Integer>();
		newSize.put(card, supply.get(card));
		for (Player eachPlayer : players) {
			setPileSizes(eachPlayer, newSize);
		}
	}

	public void returnToSupply(Card card, int count) {
		// update supply
		supply.put(card, supply.get(card) + count);
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
		onGained(player, card);
	}

	public void gainToTopOfDeck(Player player, Card card) {
		takeFromSupply(card);
		// put card on top of player's deck
		player.putOnDraw(card);
		onGained(player, card);
	}

	public void gainToHand(Player player, Card card) {
		takeFromSupply(card);
		// put card in player's hand
		player.addToHand(card);
		onGained(player, card);
	}

	public void gainFromTrash(Player player, Card card) {
		trash.remove(card);
		// put card in player's hand
		player.addToDiscard(card);
		onGained(player, card);
	}

	private void onGained(Player player, Card card) {
		player.cardsGainedDuringTurn.add(card);
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

	public void addEmbargoToken(Card card) {
		embargoTokens.put(card, embargoTokens.get(card) + 1);
		sendEmbargoTokens(card);
	}

	@SuppressWarnings("unchecked")
	private void sendEmbargoTokens(Card card) {
		JSONObject command = new JSONObject();
		command.put("command", "setEmbargoTokens");
		command.put("card", card.toString());
		command.put("numTokens", embargoTokens.get(card));
		for (Player player : players) {
			player.sendCommand(command);
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

	/**
	 * Returns a card that the player has chosen to buy, or null if they do not
	 * choose to buy anything.
	 */
	public Card promptChooseBuy(Player player, Set<Card> choiceSet) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.chooseBuy(choiceSet);
			// check that the bot is making a valid choice
			if (card != null && !choiceSet.contains(card)) {
				throw new IllegalStateException();
			}
			return card;
		}
		Card toBuy = sendPromptChooseFromSupply(player, choiceSet, "Buy Phase: Choose a card to purchase", "buyPrompt", false, "End Turn");
		recordPlayerGained(player, toBuy);
		return toBuy;
	}

	/**
	 * Returns a card that the player has chosen to gain.
	 * This choice is mandatory.
	 * This choice is the "actionPrompt" type.
	 */
	public Card promptChooseGainFromSupply(Player player, Set<Card> choiceSet, String promptMessage) {
		return promptChooseGainFromSupply(player, choiceSet, promptMessage, true, "");
	}

	/**
	 * Returns a card that the player has chosen to gain, or null if the choice
	 * is not mandatory and they do not choose to gain anything.
	 * This choice is the "actionPrompt" type.
	 */
	public Card promptChooseGainFromSupply(Player player, Set<Card> choiceSet, String promptMessage, boolean isMandatory, String noneMessage) {
		return promptChooseGainFromSupply(player, choiceSet, promptMessage, "actionPrompt", isMandatory, noneMessage);
	}

	/**
	 * Returns a card that the player has chosen to gain, or null if the choice
	 * is not mandatory and they do not choose to gain anything.
	 */
	public Card promptChooseGainFromSupply(Player player, Set<Card> choiceSet, String promptMessage, String promptType, boolean isMandatory, String noneMessage) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.chooseGainFromSupply(choiceSet, isMandatory);
			// check that the bot is making a valid choice
			if (isMandatory) {
				if (!choiceSet.contains(card)) {
					throw new IllegalStateException();
				}
			} else {
				if (card != null && !choiceSet.contains(card)) {
					throw new IllegalStateException();
				}
			}
			return card;
		}
		Card toGain = sendPromptChooseFromSupply(player, choiceSet, promptMessage, promptType, isMandatory, noneMessage);
		recordPlayerGained(player, toGain);
		return toGain;
	}

	/**
	 * Returns a card that the player has chosen for an opponent to gain.
	 * This choice is mandatory.
	 */
	public Card promptChooseOpponentGainFromSupply(Player player, Set<Card> choiceSet, String promptMessage) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.chooseOpponentGainFromSupply(choiceSet);
			// check that the bot is making a valid choice
			if (!choiceSet.contains(card)) {
				throw new IllegalStateException();
			}
			return card;
		}
		return sendPromptChooseFromSupply(player, choiceSet, promptMessage, "actionPrompt", true, "");
	}

	/**
	 * Prompts a human player to choose a card from the supply.
	 */
	@SuppressWarnings("unchecked")
	private Card sendPromptChooseFromSupply(Player player, Set<Card> choiceSet, String promptMessage, String promptType, boolean isMandatory, String noneMessage) {
		if (player instanceof Bot) {
			throw new IllegalStateException();
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

	/**
	 * Returns a card that the player has chosen to play.
	 * This choice is mandatory.
	 */
	public Card promptChoosePlay(Player player, Set<Card> choiceSet, String promptMessage) {
		return promptChoosePlay(player, choiceSet, promptMessage, true, "");
	}

	/**
	 * Returns a card that the player has chosen to play, or null if they
	 * choose not to play any action.
	 */
	public Card promptChoosePlay(Player player, Set<Card> choiceSet, String promptMessage, boolean isMandatory, String noneMessage) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.choosePlay(choiceSet, isMandatory);
			// check that the bot is making a valid choice
			if (isMandatory) {
				if (!choiceSet.contains(card)) {
					throw new IllegalStateException();
				}
			} else {
				if (card != null && !choiceSet.contains(card)) {
					throw new IllegalStateException();
				}
			}
			return card;
		}
		return sendPromptChooseFromHand(player, choiceSet, promptMessage, "actionPrompt", isMandatory, noneMessage);
	}

	/**
	 * Returns a card that the player has chosen to trash from their hand.
	 * This choice is mandatory.
	 */
	public Card promptChooseTrashFromHand(Player player, Set<Card> choiceSet, String promptMessage) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.chooseTrashFromHand(choiceSet);
			// check that the bot is making a valid choice
			if (!choiceSet.contains(card)) {
				throw new IllegalArgumentException();
			}
			return card;
		}
		return sendPromptChooseFromHand(player, choiceSet, promptMessage, "actionPrompt", true, "");
	}

	/**
	 * Returns a card that the player has chosen to trash from their hand.
	 * This choice is mandatory.
	 */
	public Card promptChooseIslandFromHand(Player player, Set<Card> choiceSet, String promptMessage) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.chooseIslandFromHand(choiceSet);
			// check that the bot is making a valid choice
			if (!choiceSet.contains(card)) {
				throw new IllegalArgumentException();
			}
			return card;
		}
		return sendPromptChooseFromHand(player, choiceSet, promptMessage, "actionPrompt", true, "");
	}

	/**
	 * Returns a card that the player has chosen to put on top of their deck.
	 * This choice is the "actionPrompt" type.
	 */
	public Card promptChoosePutOnDeck(Player player, Set<Card> choiceSet, String promptMessage) {
		return promptChoosePutOnDeck(player, choiceSet, promptMessage, "actionPrompt");
	}

	/**
	 * Returns a card that the player has chosen to put on top of their deck.
	 * This choice is mandatory.
	 */
	public Card promptChoosePutOnDeck(Player player, Set<Card> choiceSet, String promptMessage, String promptType) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.choosePutOnDeck(choiceSet);
			// check that the bot is making a valid choice
			if (!choiceSet.contains(card)) {
				throw new IllegalArgumentException();
			}
			return card;
		}
		return sendPromptChooseFromHand(player, choiceSet, promptMessage, promptType, true, "");
	}

	/**
	 * Returns a card that the player has chosen to pass to an opponent.
	 * This choice is mandatory.
	 */
	public Card promptChoosePassToOpponent(Player player, Set<Card> choiceSet, String promptMessage, String promptType) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.choosePassToOpponent(choiceSet);
			// check that the bot is making a valid choice
			if (!choiceSet.contains(card)) {
				throw new IllegalArgumentException();
			}
			return card;
		}
		return sendPromptChooseFromHand(player, choiceSet, promptMessage, promptType, true, "");
	}

	/**
	 * Returns a card that the player has chosen to reveal as an attack
	 * reaction, or null if they choose to reveal no reaction.
	 */
	public Card promptChooseRevealAttackReaction(Player player, Set<Card> choiceSet) {
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card card = bot.chooseRevealAttackReaction(choiceSet);
			// check that the bot is making a valid choice
			if (card != null && !choiceSet.contains(card)) {
				throw new IllegalArgumentException();
			}
			return card;
		}
		return sendPromptChooseFromHand(player, choiceSet, "Choose a reaction", "reactionPrompt", false, "No Reaction");
	}

	/**
	 * Prompts a human player to choose a card from their hand.
	 */
	@SuppressWarnings("unchecked")
	private Card sendPromptChooseFromHand(Player player, Set<Card> choiceSet, String promptMessage, String promptType, boolean isMandatory, String noneMessage) {
		if (player instanceof Bot) {
			throw new IllegalStateException();
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
			if (isMandatory) {
				return choiceSet.iterator().next();
			} else {
				return null;
			}
		}
	}

	/**
	 * Returns a list of cards that the player has chosen to discard.
	 * This prompt is mandatory.
	 */
	public List<Card> promptDiscardNumber(Player player, int number, String cause, String promptType) {
		return promptDiscardNumber(player, number, true, cause, promptType);
	}

	/**
	 * Returns a list of cards that the player has chosen to discard.
	 * This prompt is the "actionPrompt" type.
	 */
	public List<Card> promptDiscardNumber(Player player, int number, boolean isMandatory, String cause) {
		return promptDiscardNumber(player, number, isMandatory, cause, "actionPrompt");
	}

	/**
	 * Returns a list of cards that the player has chosen to discard.
	 */
	public List<Card> promptDiscardNumber(Player player, int number, boolean isMandatory, String cause, String promptType) {
		if (player.getHand().size() < number) {
			number = player.getHand().size();
		}
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			List<Card> toDiscard = bot.discardNumber(number, isMandatory);
			// check that the bot's choice is valid
			if (isMandatory && toDiscard.size() != number) {
				throw new IllegalStateException();
			}
			List<Card> handCopy = new ArrayList<Card>(bot.getHand());
			for (Card card : toDiscard) {
				if (!handCopy.remove(card)) {
					throw new IllegalStateException();
				}
			}
			return toDiscard;
		}
		return sendPromptDiscardNumber(player, number, isMandatory, cause, promptType, "discard");
	}

	/**
	 * Returns a list of cards that the player has chosen to trash.
	 * This choice is mandatory.
	 * This choice is the "actionPrompt" type.
	 */
	public List<Card> promptTrashNumber(Player player, int number, String cause) {
		return promptTrashNumber(player, number, true, cause, "actionPrompt");
	}

	/**
	 * Returns a list of cards that the player has chosen to trash.
	 * This choice is the "actionPrompt" type.
	 */
	public List<Card> promptTrashNumber(Player player, int number, boolean isMandatory, String cause) {
		return promptTrashNumber(player, number, isMandatory, cause, "actionPrompt");
	}

	/**
	 * Returns a list of cards that the player has chosen to trash.
	 */
	public List<Card> promptTrashNumber(Player player, int number, boolean isMandatory, String cause, String promptType) {
		if (player.getHand().size() < number) {
			number = player.getHand().size();
		}
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			List<Card> toTrash = bot.trashNumber(number, isMandatory);
			// check that the bot's choice is valid
			if (isMandatory && toTrash.size() != number) {
				throw new IllegalStateException();
			}
			List<Card> handCopy = new ArrayList<Card>(bot.getHand());
			for (Card card : toTrash) {
				if (!handCopy.remove(card)) {
					throw new IllegalStateException();
				}
			}
			return toTrash;
		}
		return sendPromptDiscardNumber(player, number, isMandatory, cause, promptType, "trash");
	}

	/**
	 * Returns a list of cards that the player has chosen to put from their
	 * hand on top of their deck.
	 * This prompt is mandatory.
	 * This prompt is the "actionPrompt" type.
	 */
	public List<Card> promptPutNumberOnDeck(Player player, int number, String cause) {
		return promptPutNumberOnDeck(player, number, cause, "actionPrompt");
	}

	/**
	 * Returns a list of cards that the player has chosen to put from their
	 * hand on top of their deck.
	 * This prompt is mandatory.
	 */
	public List<Card> promptPutNumberOnDeck(Player player, int number, String cause, String promptType) {
		if (player.getHand().size() < number) {
			number = player.getHand().size();
		}
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			List<Card> toPutOnDeck = bot.putNumberOnDeck(number);
			// check that the bot's choice is valid
			if (toPutOnDeck.size() != number) {
				throw new IllegalStateException();
			}
			List<Card> handCopy = new ArrayList<Card>(bot.getHand());
			for (Card card : toPutOnDeck) {
				if (!handCopy.remove(card)) {
					throw new IllegalStateException();
				}
			}
			return toPutOnDeck;
		}
		return sendPromptDiscardNumber(player, number, true, cause, promptType, "draw");
	}

	/**
	 * Prompts a human player for a number of cards to discard, trash, or put
	 * on top of their deck, all from their hand.
	 * destination determines which of these three the prompt is.
	 * "discard" -> discard
	 * "trash" -> trash
	 * "draw" -> put on top of deck
	 */
	@SuppressWarnings("unchecked")
	public List<Card> sendPromptDiscardNumber(Player player, int number, boolean isMandatory, String cause, String promptType, String destination) {
		if (player instanceof Bot) {
			throw new IllegalStateException();
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
	public void newTurnMessage(Player player, String str) {
		JSONObject command = new JSONObject();
		command.put("command", "newTurnMessage");
		command.put("text", str);
		player.sendCommand(command);
	}

	public void newTurnMessage(Player player) {
		newTurnMessage(player, "<span class=\"turnTitle\">-- Your Turn " + (player.turns + 1) + " --</span>");
		for (Player opponent : getOpponents(player)) {
			newTurnMessage(opponent, "<span class=\"turnTitle\">-- " + player.username + "'s Turn " + (player.turns + 1) + " --</span>");
		}
	}

	@SuppressWarnings("unchecked")
	public void message(Player player, String str) {
		JSONObject command = new JSONObject();
		command.put("command", "message");
		command.put("text", str);
		command.put("indent", messageIndent);
		player.sendCommand(command);
	}

	public void messageAll(String str) {
		for (Player player : players) {
			message(player, str);
		}
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

	// record player choices for later data mining
	Map<Player, List<Card>> gainRecords;

	private void initRecords() {
		gainRecords = new HashMap<Player, List<Card>>();
		for (Player player : players) {
			gainRecords.put(player, new ArrayList<Card>());
		}
	}

	private void recordPlayerGained(Player player, Card card) {
		if (card != null) {
			gainRecords.get(player).add(card);
		}
	}

	private void recordPlayerWin(Player player) {
		List<Card> gainRecord = gainRecords.get(player);
		if (!gainRecord.isEmpty()) {
			server.recordWinningStrategy(new HashSet<Card>(this.kingdomCards), gainRecord);
		}
	}

}
