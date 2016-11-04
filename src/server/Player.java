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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Player {

	public static final Comparator<Card> HAND_ORDER_COMPARATOR = new Comparator<Card>() {
		@Override
		public int compare(Card c1, Card c2) {
			int c1_type, c2_type;
			if (c1.isAction) {
				c1_type = 1;
			} else if (c1.isTreasure) {
				c1_type = 2;
			} else if (c1.isVictory) {
				c1_type = 3;
			} else {
				c1_type = 4;
			}
			if (c2.isAction) {
				c2_type = 1;
			} else if (c2.isTreasure) {
				c2_type = 2;
			} else if (c2.isVictory) {
				c2_type = 3;
			} else {
				c2_type = 4;
			}
			if (c1_type != c2_type) {
				// actions < coins < victories < curses 
				return c1_type - c2_type;
			} else {
				// coins and victories
				if (c1_type == 2 || c1_type == 3) {
					// order by cost, which is useful for upgrading
					return c2.cost() - c1.cost();
				}
				// order alphabetically
				return c1.toString().compareTo(c2.toString());
			}
		}
	};

	public PlayerWebSocketHandler conn;
	public List<JSONObject> commands = new ArrayList<JSONObject>();

	public BlockingQueue<Object> responses = new LinkedBlockingQueue<Object>();
	public boolean forfeit;

	public void sendCommand(JSONObject command){
		sendCommand(command, false);
	}

	public void issueCommand(JSONObject command) {
		sendCommand(command, true);
	}

	protected synchronized void sendCommand(JSONObject command, boolean autoIssue) {
		if (!forfeit) {
			// premature optimization!
			String commandType = (String) command.get("command");
			if ("message".equals(commandType) || "newTurnMessage".equals(commandType) || "setPileSizes".equals(commandType) || "setCardCosts".equals(commandType) || "setTradeRouteToken".equals(commandType)) {
				commands.add(command);
			} else {
				Iterator<JSONObject> iter = commands.iterator();
				while (iter.hasNext()) {
					if (commandType.equals(iter.next().get("command"))) {
						iter.remove();
					}
				}
				commands.add(command);
			}
			if (autoIssue) {
				issueCommands();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized void issueCommands() {
		if (!forfeit && commands.size() > 0) {
			JSONArray commandArray = new JSONArray();
			for (JSONObject command : commands) {
				commandArray.add(command);
			}
			commands.clear();
			conn.send(commandArray.toJSONString());
		}
	}

	public void receiveResponse(Object response) {
		// blocking queue cannot accept null
		if (response != null) {
			responses.add(response);
		}
	}

	public String username;

	public Game game;

	private List<Card> draw = new ArrayList<Card>();
	private List<Card> hand = new ArrayList<Card>();
	private List<Card> play = new ArrayList<Card>();
	private List<Card> discard = new ArrayList<Card>();

	private List<Card> nativeVillageMat = new ArrayList<Card>();
	private List<Card> islandMat = new ArrayList<Card>();
	private int pirateShipTokens = 0;

	private int victoryTokens = 0;

	private List<DurationEffect> durationEffects = new ArrayList<>();
	private List<Card> durationSetAsideCards = new ArrayList<>();
	private List<Card> resolvedDurationCards = new ArrayList<>();

	private int actions;
	private int buys;
	private int coins;

	public int turns;

	// keep track of gained cards for Smugglers
	public Set<Card> cardsGainedDuringTurn = new HashSet<Card>();

	// keep track of Fool's Golds played
	public boolean playedFoolsGoldThisTurn;

	public Player(PlayerWebSocketHandler conn) {
		this.conn = conn;
	}

	public void startGame(boolean usingShelters) {
		draw.clear();
		hand.clear();
		play.clear();
		discard.clear();
		nativeVillageMat.clear();
		islandMat.clear();
		pirateShipTokens = 0;
		victoryTokens = 0;
		durationEffects.clear();
		durationSetAsideCards.clear();
		resolvedDurationCards.clear();
		turns = 0;
		cardsGainedDuringTurn.clear();
		if (usingShelters) {
			draw.add(Card.HOVEL);
			draw.add(Card.NECROPOLIS);
			draw.add(Card.OVERGROWN_ESTATE);
		} else {
			for (int i = 0; i < 3; i++) {
				draw.add(Card.ESTATE);
			}
		}
		for (int i = 0; i < 7; i++) {
			draw.add(Card.COPPER);
		}
		Collections.shuffle(draw);
		newTurn();
	}

	public void newTurn() {
		actions = 1;
		buys = 1;
		coins = 0;
		playedFoolsGoldThisTurn = false;
		// reset UI state
		resetHandOrder();
		// drawing a new hand automatically sends the player their hand and coins
		if (!hasExtraTurn()) {
			drawIntoHand(5);
		} else {
			drawIntoHand(3);
		}
	}

	public boolean hasExtraTurn() {
		return durationEffects.stream().anyMatch(effect -> effect.card == Card.OUTPOST);
	}

	public boolean isTakingExtraTurn() {
		return resolvedDurationCards.contains(Card.OUTPOST);
	}

	void startNewTurn() {
		cardsGainedDuringTurn.clear();
	}

	void cleanupHand() {
		discard.addAll(hand);
		hand.clear();
	}

	void cleanupPlay() {
        play = play.stream().map(c -> c.isBandOfMisfits ? Card.BAND_OF_MISFITS : c).collect(Collectors.toList());
		discard.addAll(play);
		play.clear();
		sendPlay();
        resolvedDurationCards = resolvedDurationCards.stream().map(c -> c.isBandOfMisfits ? Card.BAND_OF_MISFITS : c).collect(Collectors.toList());
		discard.addAll(resolvedDurationCards);
		resolvedDurationCards.clear();
		sendDiscardSize();
		newTurn();
	}

	boolean hasPlayableAction() {
		for (Card card : hand) {
			if (card.isAction) {
				return true;
			}
		}
		return false;
	}

	public int getActions() {
		return actions;
	}

	public void setActions(int actions) {
		this.actions = actions;
		sendActions();
	}

	public void addActions(int toAdd) {
		setActions(actions + toAdd);
	}

	@SuppressWarnings("unchecked")
	public void sendActions() {
		JSONObject setActions = new JSONObject();
		setActions.put("command", "setActions");
		setActions.put("actions", Integer.toString(actions));
		sendCommand(setActions);
	}

	public int getBuys() {
		return buys;
	}

	public void setBuys(int buys) {
		this.buys = buys;
		sendBuys();
	}

	public void addBuys(int toAdd) {
		setBuys(buys + toAdd);
	}

	@SuppressWarnings("unchecked")
	public void sendBuys() {
		JSONObject setBuys = new JSONObject();
		setBuys.put("command", "setBuys");
		setBuys.put("buys", Integer.toString(buys));
		sendCommand(setBuys);
	}

	public int getCoins() {
		return coins;
	}

	public void setCoins(int coins) {
		this.coins = coins;
		sendCoins();
	}

	public void addCoins(int toAdd) {
		setCoins(coins + toAdd);
	}

	public int getUsableCoins() {
		if (!isAutoplayingTreasures()) {
			return getCoins();
		} else {
			// start with current coins
			int usableCoins = getCoins();
			int numTreasuresInPlay = 0;
			int numBanks = 0;
			int numFoolsGolds = 0;
			// add the value of all unplayed non-bank, non-fools-gold treasures
			for (Card card : hand) {
				if (card == Card.BANK) {
					numBanks++;
				} else if (card.isTreasure) {
					if (card == Card.FOOLS_GOLD) {
						numFoolsGolds++;
					} else {
						usableCoins += card.treasureValue(game);
					}
					numTreasuresInPlay++;
				}
			}
			// add the number of treasures currently in play to the number of non-bank treasures
			for (Card card : play) {
				if (card.isTreasure) {
					numTreasuresInPlay++;
				}
			}
			// add the value of all Banks
			while (numBanks != 0) {
				usableCoins += (numTreasuresInPlay + 1);
				numBanks--;
				numTreasuresInPlay++;
			}
			// handle Merchant +$1 on first Silver
			if (play.contains(Card.MERCHANT) && !game.playedSilverThisTurn && hand.contains(Card.SILVER)) {
				usableCoins += numberInPlay(Card.MERCHANT);
			}
			// handle Diadem +$1 per unused action
			if (hand.contains(Card.DIADEM)) {
				usableCoins += actions;
			}
			// handle Fool's Gold
			if (numFoolsGolds != 0) {
				// if no Fool's Golds have been played yet this turn
				if (!playedFoolsGoldThisTurn) {
					// $1 for the first Fool's Gold, then $4 for each additional
					usableCoins += 1 + 4 * (numFoolsGolds - 1);
				} else {
					usableCoins += 4 * numFoolsGolds;
				}
			}
			return usableCoins;
		}
	}

	public boolean isAutoplayingTreasures() {
		return !hand.contains(Card.QUARRY) &&
				!hand.contains(Card.CONTRABAND) &&
				!hand.contains(Card.VENTURE) &&
				!(game.supply.containsKey(Card.GRAND_MARKET) && hand.contains(Card.COPPER)) &&
				!hand.contains(Card.HORN_OF_PLENTY) &&
				!hand.contains(Card.ILL_GOTTEN_GAINS) &&
				!hand.contains(Card.SPOILS) && !hand.contains(Card.COUNTERFEIT);
	}

	@SuppressWarnings("unchecked")
	public void sendCoins() {
		JSONObject command = new JSONObject();
		command.put("command", "setCoins");
		command.put("coins", "$" + Integer.toString(getUsableCoins()));
		if (!isAutoplayingTreasures()) {
			command.put("notAutoplayingTreasures", true);
		}
		sendCommand(command);
	}

	@SuppressWarnings("unchecked")
	public void sendDrawSize() {
		JSONObject command = new JSONObject();
		command.put("command", "setDrawSize");
		command.put("size", draw.size());
		sendCommand(command);
	}

	@SuppressWarnings("unchecked")
	public void sendDiscardSize() {
		JSONObject command = new JSONObject();
		command.put("command", "setDiscardSize");
		command.put("size", discard.size());
		sendCommand(command);
	}

	public List<Card> getHand() {
		return hand;
	}

	public int numberInHand(Card card) {
		int num = 0;
		for (Card inHand : hand) {
			if (inHand == card) {
				num++;
			}
		}
		return num;
	}

	public boolean handContains(List<Card> cards) {
		List<Card> handCopy = new ArrayList<>(hand);
		for (Card card : cards) {
			if (!handCopy.remove(card)) {
				return false;
			}
		}
		return true;
	}

	// the order of stacks displayed in the player's hand.
	// newly drawn cards that do not already have a stack are always placed at the end.
	// this prevents the hand from being drastically rearranged in the middle of the player's turn
	private List<Card> handOrder = new ArrayList<Card>();
	private void resetHandOrder() {
		handOrder.clear();
	}

	@SuppressWarnings("unchecked")
	public void sendHand() {
		JSONObject command = new JSONObject();
		command.put("command", "setHand");
		// count cards of each type in hand
		Map<Card, Integer> counts = new HashMap<Card, Integer>();
		for (Card card : hand) {
			if (!counts.containsKey(card)) {
				counts.put(card, 1);
			} else {
				counts.put(card, counts.get(card) + 1);
			}
		}
		// remove cards that are no longer in hand from the hand order
		Iterator<Card> iter = handOrder.iterator();
		while (iter.hasNext()) {
			Card card = iter.next();
			if (!counts.containsKey(card)) {
				iter.remove();
			}
		}
		// add new cards to the end of the hand order
		Set<Card> newCards = new HashSet<Card>(counts.keySet());
		for (Card oldCard : handOrder) {
			newCards.remove(oldCard);
		}
		List<Card> newCardList = new ArrayList<Card>(newCards); 
		Collections.sort(newCardList, HAND_ORDER_COMPARATOR);
		handOrder.addAll(newCardList);
		// send card counts in order of handOrder
		JSONArray hand = new JSONArray();
		for (Card card : handOrder) {
			JSONObject stack = new JSONObject();
			stack.put("name", card.toString());
			stack.put("count", counts.get(card));
			hand.add(stack);
		}
		command.put("hand", hand);
		sendCommand(command);
		// setting hand may change available coins
		sendCoins();
	}

	public void addToHand(Card card) {
		hand.add(card);
		sendHand();
	}

	public void addToHand(Card card, int count) {
		for (int i = 0; i < count; i++) {
			hand.add(card);
		}
		sendHand();
	}

	public void addToHand(List<Card> cards) {
		hand.addAll(cards);
		sendHand();
	}

	public void removeFromHand(Card card) {
		hand.remove(card);
		sendHand();
	}

	public void removeFromHand(List<Card> cards) {
		for (Card card : cards) {
			hand.remove(card);
		}
		sendHand();
	}

	public void putFromHandIntoPlay(Card card) {
		removeFromHand(card);
		addToPlay(card);
	}

	public void putFromHandIntoDiscard(Card card) {
		removeFromHand(card);
		addToDiscard(card);
	}

	public void putFromHandIntoDiscard(List<Card> cards) {
		removeFromHand(cards);
		addToDiscard(cards);
	}

	public void putFromHandOntoDraw(Card card) {
		removeFromHand(card);
		putOnDraw(card);
	}

	public List<Card> getDiscard() {
		return discard;
	}

	public void addToDiscard(Card card) {
		addToDiscard(card, true);
	}

	public void addToDiscard(Card card, boolean triggersTunnelReaction) {
		card = card.isBandOfMisfits ? Card.BAND_OF_MISFITS : card;
		if (triggersTunnelReaction && card == Card.TUNNEL) {
			handleDiscardedTunnels(1);
		}
		discard.add(card);
		sendDiscardSize();
	}

	public void addToDiscard(List<Card> cards) {
		addToDiscard(cards, true);
	}

	public void addToDiscard(List<Card> cards, boolean triggersTunnelReaction) {
		cards = cards.stream().map(c -> c.isBandOfMisfits ? Card.BAND_OF_MISFITS : c).collect(Collectors.toList());
		if (triggersTunnelReaction && cards.contains(Card.TUNNEL)) {
			int numTunnels = (int) cards.stream().filter(c -> c == Card.TUNNEL).count();
			handleDiscardedTunnels(numTunnels);
		}
		discard.addAll(cards);
		sendDiscardSize();
	}

	private void handleDiscardedTunnels(int numTunnels) {
		game.messageIndent++;
		for (int i = 0; i < numTunnels && game.supply.get(Card.GOLD) != 0; i++) {
			if (chooseRevealTunnel()) {
				game.messageAll("revealing the " + Card.TUNNEL.htmlNameRaw() + " and gaining " + Card.GOLD.htmlName());
				game.gain(this, Card.GOLD);
			} else {
				break;
			}
		}
		game.messageIndent--;
	}

	protected boolean chooseRevealTunnel() {
		int choice = game.promptMultipleChoice(this, "Tunnel: Reveal the " + Card.TUNNEL.htmlNameRaw() + " and gain " + Card.GOLD.htmlName() + "?", "reactionPrompt", new String[] {"Yes", "No"});
		return (choice == 0);
	}

	public List<Card> getDraw() {
		return draw;
	}

	public void putOnDraw(Card card) {
		putInDraw(card, 0);
	}

	public void putInDraw(Card card, int index) {
		draw.add(index, card);
		sendDrawSize();
	}

	public void putOnDraw(List<Card> cards) {
		draw.addAll(0, cards);
		sendDrawSize();
	}

	public void shuffleIntoDraw(List<Card> cards) {
		draw.addAll(cards);
		Collections.shuffle(draw);
		sendDrawSize();
	}

	public List<Card> takeFromDraw(int n) {
		List<Card> drawn = new ArrayList<Card>();
		// if draw pile is too small, take all of it and replace it with shuffled discard pile
		if (draw.size() < n) {
			drawn.addAll(draw);
			draw.clear();
			n -= drawn.size();
			replaceDrawWithShuffledDiscard();
		}
		// if draw pile is still to small, take as much of draw pile as possible
		if (draw.size() < n) {
			drawn.addAll(draw);
			draw.clear();
		} else {
			// otherwise, just take the remaining number
			drawn.addAll(draw.subList(0, n));
			draw = draw.subList(n, draw.size());
		}
		sendDrawSize();
		return drawn;
	}

	private void replaceDrawWithShuffledDiscard() {
		Collections.shuffle(discard);
		draw.addAll(discard);
		discard.clear();
		if (draw.size() > 0) {
			// announce that this player shuffled
			game.message(this, "(you shuffle)");
			game.messageOpponents(this, "(" + username + " shuffles)");
			sendDiscardSize();
		}
	}

	public List<Card> drawIntoHand(int n) {
		List<Card> drawn = takeFromDraw(n);
		hand.addAll(drawn);
		sendHand();
		return drawn;
	}

	public Card bottomOfDeck() {
		if (draw.isEmpty()) {
			replaceDrawWithShuffledDiscard();
		}
		if (!draw.isEmpty()) {
			return draw.get(draw.size() - 1);
		} else {
			return null;
		}
	}

	public Card takeFromBottomOfDeck() {
		Card card = draw.remove(draw.size() - 1);
		sendDrawSize();
		return card;
	}

	public List<Card> getPlay() {
		return play;
	}

	public int numberInPlay(Card card) {
		int num = 0;
		for (Card inPlay : play) {
			if (inPlay == card) {
				num++;
			}
		}
		return num;
	}

	public void addToPlay(Card card) {
		play.add(card);
		sendPlay();
	}

	public void removeFromPlay(Card card) {
		play.remove(card);
		sendPlay();
	}

	public void removeFromPlay(List<Card> cards) {
		for (Card card : cards) {
			play.remove(card);
		}
		sendPlay();
	}

	public List<Card> removeAllTreasuresFromPlay() {
		List<Card> treasures = new ArrayList<Card>();
		for (Iterator<Card> iter = play.iterator(); iter.hasNext(); ) {
			Card card = iter.next();
			if (card.isTreasure) {
				iter.remove();
				treasures.add(card);
			}
		}
		if (!treasures.isEmpty()) {
			sendPlay();
		}
		return treasures;
	}

	public List<Card> allCardsInPlay() {
		List<Card> cardsInPlay = new ArrayList<>();
		cardsInPlay.addAll(play);
		// for Horn of Plenty, duration cards played last turn count as in play (but not their modifiers!)
		resolvedDurationCards.stream().filter(c -> c.isDuration).forEach(cardsInPlay::add);
		return cardsInPlay;
	}

	public void removeFromDiscard(List<Card> cards) {
		for (Card card : cards) {
			discard.remove(card);
		}
		sendDiscardSize();
	}

	public void removeFromDiscard(Card card) {
        removeFromDiscard(card, 1);
    }

	public void removeFromDiscard(Card card, int count) {
		for (Iterator<Card> iter = discard.iterator(); count != 0 && iter.hasNext(); ) {
			if (iter.next() == card) {
				iter.remove();
				count--;
			}
		}
		sendDiscardSize();
	}

	@SuppressWarnings("unchecked")
	public void sendPlay() {
		JSONObject command = new JSONObject();
		command.put("command", "setInPlay");
		if (!play.isEmpty()) {
			command.put("contents", Card.htmlList(play));
		}
		sendCommand(command);
	}

	public void putOnNativeVillageMat(Card card) {
		nativeVillageMat.add(card);
		sendNativeVillageMat();
	}

	public List<Card> takeAllFromNativeVillageMat() {
		List<Card> taken = new ArrayList<>(nativeVillageMat);
		nativeVillageMat.clear();
		sendNativeVillageMat();
		return taken;
	}

	@SuppressWarnings("unchecked")
	private void sendNativeVillageMat() {
		JSONObject command = new JSONObject();
		command.put("command", "setNativeVillageMat");
		if (!nativeVillageMat.isEmpty()) {
			command.put("contents", Card.htmlList(nativeVillageMat));
		}
		sendCommand(command);
	}

	public void putOnIslandMat(Card card) {
        card = card.isBandOfMisfits ? Card.BAND_OF_MISFITS : card;
		islandMat.add(card);
		sendIslandMat();
	}

	@SuppressWarnings("unchecked")
	private void sendIslandMat() {
		JSONObject command = new JSONObject();
		command.put("command", "setIslandMat");
		if (!islandMat.isEmpty()) {
			command.put("contents", Card.htmlList(islandMat));
		}
		sendCommand(command);
	}

	public void addPirateShipToken() {
		pirateShipTokens++;
		sendPirateShipMat();
	}

	public int getPirateShipTokens() {
		return pirateShipTokens;
	}

	@SuppressWarnings("unchecked")
	private void sendPirateShipMat() {
		JSONObject command = new JSONObject();
		command.put("command", "setPirateShipMat");
		if (pirateShipTokens != 0) {
			command.put("contents", "$" + pirateShipTokens);
		}
		sendCommand(command);
	}

	public void addVictoryTokens(int numTokens) {
		victoryTokens += numTokens;
		sendVictoryTokenMat();
	}

	public int getVictoryTokens() {
		return victoryTokens;
	}

	@SuppressWarnings("unchecked")
	private void sendVictoryTokenMat() {
		JSONObject command = new JSONObject();
		command.put("command", "setVictoryTokenMat");
		if (victoryTokens != 0) {
			command.put("contents", victoryTokens + " VP");
		}
		sendCommand(command);
	}

	public List<DurationEffect> getDurationEffects() {
		return durationEffects;
	}

	public void addDurationEffect(Card card) {
		addDurationEffect(card, null);
	}

	public void addDurationEffect(Card card, List<Card> havenedCards) {
		DurationEffect effect = new DurationEffect();
		effect.card = card;
		effect.havenedCards = havenedCards;
		if (havenedCards != null) {
			durationSetAsideCards.addAll(havenedCards);
		}
		durationEffects.add(effect);
		sendDurations();
	}

	public void addDurationSetAside(Card card) {
		durationSetAsideCards.add(card);
		sendDurations();
	}

	public void removeDurationSetAside(Card card) {
		durationSetAsideCards.remove(card);
		sendDurations();
	}

	public void cleanupDurations() {
		durationEffects.clear();
		resolvedDurationCards.addAll(durationSetAsideCards);
		durationSetAsideCards.clear();
		sendDurations();
	}

	@SuppressWarnings("unchecked")
	public void sendDurations() {
		JSONObject command = new JSONObject();
		command.put("command", "setDurations");
		command.put("contents", durationSetAsideCards.isEmpty() ? "" : Card.htmlList(durationSetAsideCards));
		sendCommand(command);
	}

	public List<Card> getDeck() {
		List<Card> deck = new ArrayList<Card>();
		deck.addAll(draw);
		deck.addAll(hand);
		deck.addAll(play);
		deck.addAll(discard);
		deck.addAll(nativeVillageMat);
		deck.addAll(islandMat);
		deck.addAll(durationSetAsideCards);
		deck.addAll(resolvedDurationCards);
		return deck;
	}

}
