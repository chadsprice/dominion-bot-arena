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
	public List<JSONObject> commands;

	public BlockingQueue<Object> responses;
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
			Iterator<JSONObject> iter = commands.iterator();
			while (iter.hasNext()) {
				String commandType = (String) iter.next().get("command");
				if (!commandType.equals("message") && !commandType.equals("setPileSizes") && commandType.equals(command.get("command"))) {
					iter.remove();
				}
			}
			commands.add(command);
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
		responses.add(response);
	}

	public String username;

	public Game game;

	private List<Card> draw;
	private List<Card> hand;
	private List<Card> play;
	private List<Card> discard;

	private List<Card> nativeVillageMat;

	private int actions;
	private int buys;
	private int extraCoins;

	public int turns;

	public Player(PlayerWebSocketHandler conn) {
		this.conn = conn;
		commands = new ArrayList<JSONObject>();
		responses = new LinkedBlockingQueue<Object>();
		draw = new ArrayList<Card>();
		hand = new ArrayList<Card>();
		play = new ArrayList<Card>();
		discard = new ArrayList<Card>();
		nativeVillageMat = new ArrayList<Card>();
	}

	public List<Card> takeFromDraw(int n) {
		List<Card> drawn = new ArrayList<Card>();
		// if draw pile is too small, take all of it and replace it with shuffled discard pile
		if (draw.size() < n) {
			drawn.addAll(draw);
			draw.clear();
			n -= drawn.size();
			Collections.shuffle(discard);
			draw.addAll(discard);
			discard.clear();
			if (draw.size() > 0) {
				// announce that this player shuffled
				game.message(this, "(you shuffle)");
				game.messageOpponents(this, "(" + username + " shuffles)");
			}
			sendDiscardSize();
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

	public List<Card> drawIntoHand(int n) {
		List<Card> drawn = takeFromDraw(n);
		hand.addAll(drawn);
		sendHand();
		return drawn;
	}

	public void startGame() {
		draw.clear();
		hand.clear();
		play.clear();
		discard.clear();
		turns = 0;
		for (int i = 0; i < 3; i++) {
			draw.add(Card.ESTATE);
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
		extraCoins = 0;
		// drawing a new hand automatically sends the player their hand and coins
		resetHandOrder();
		drawIntoHand(5);
	}

	public void cleanup() {
		discard.addAll(play);
		play.clear();
		discard.addAll(hand);
		hand.clear();
		sendDiscardSize();
		newTurn();
	}

	public boolean hasPlayableAction() {
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
		int coins = extraCoins;
		for (Card card : hand) {
			if (card.isTreasure) {
				coins += card.treasureValue(game);
			}
		}
		return coins;
	}

	public void setExtraCoins(int extraCoins) {
		this.extraCoins = extraCoins;
		sendCoins();
	}

	public void addExtraCoins(int toAdd) {
		setExtraCoins(extraCoins + toAdd);
	}

	@SuppressWarnings("unchecked")
	public void sendCoins() {
		JSONObject setCoins = new JSONObject();
		setCoins.put("command", "setCoins");
		setCoins.put("coins", "$" + Integer.toString(getCoins()));
		sendCommand(setCoins);
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
		hand.remove(card);
		play.add(card);
		sendHand();
	}

	public void putFromHandIntoDiscard(List<Card> cards) {
		removeFromHand(cards);
		discard.addAll(cards);
		sendDiscardSize();
	}

	public List<Card> getDiscard() {
		return discard;
	}

	public void addToDiscard(Card card) {
		discard.add(card);
		sendDiscardSize();
	}

	public void addToDiscard(List<Card> cards) {
		discard.addAll(cards);
		sendDiscardSize();
	}

	public List<Card> getDraw() {
		return draw;
	}

	public void putOnDraw(Card card) {
		draw.add(0, card);
		sendDrawSize();
	}

	public void putOnDraw(List<Card> cards) {
		draw.addAll(0, cards);
		sendDrawSize();
	}

	public List<Card> getPlay() {
		return play;
	}

	public void removeFromPlay(Card card) {
		play.remove(card);
	}

	public void putOnNativeVillageMat(Card card) {
		nativeVillageMat.add(card);
		sendNativeVillageMat();
	}

	public List<Card> takeAllFromNativeVillageMat() {
		List<Card> taken = new ArrayList<Card>(nativeVillageMat);
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

	public List<Card> getDeck() {
		List<Card> deck = new ArrayList<Card>();
		deck.addAll(draw);
		deck.addAll(hand);
		deck.addAll(play);
		deck.addAll(discard);
		deck.addAll(nativeVillageMat);
		return deck;
	}

}
