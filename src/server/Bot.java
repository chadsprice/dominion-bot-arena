package server;

import java.util.*;
import java.util.Map.Entry;

import bots.*;
import org.json.simple.JSONObject;

import cards.Smugglers;

public class Bot extends Player {

	public static Map<String, Class<? extends Bot>> botsByName = new HashMap<String, Class<? extends Bot>>();

	public static void initializeBots() {
		include(Bot.class);
		include(BankWharfBot.class);
		include(BigNothing.class);
		include(BigSmithy.class);
		include(BmLibraryBot.class);
		include(BmMasqueradeBot.class);
		include(ChapelWitchBot.class);
		include(MimicBot.class);
	}

	private static void include(Class<? extends Bot> botClass) {
		try {
			botsByName.put(botClass.newInstance().botName(), botClass);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException();
		} catch (InstantiationException e) {
			throw new IllegalStateException();
		}
	}

	public static Bot newBotFromName(String botName) {
		if (botsByName.containsKey(botName)) {
			try {
				return botsByName.get(botName).newInstance();
			} catch (IllegalAccessException e) {
				throw new IllegalStateException();
			} catch (InstantiationException e) {
				throw new IllegalStateException();
			}
		} else {
			// when given an unknown bot, default to BigMoney
			return new Bot();
		}
	}

	private static final Comparator<Card> COST_ORDER_COMPARATOR = new Comparator<Card>() {
		@Override
		public int compare(Card c1, Card c2) {
			// order by highest cost
			return c2.cost() - c1.cost();
		}
	};

	@Override
	protected void sendCommand(JSONObject command, boolean autoIssue) {}

	@Override
	public void issueCommands() {}

	public Bot() {
		super(null);
		setName();
	}

	public String botName() {
		return "BigMoney";
	}

	protected void setName() {
		username = "<span class=\"botName\">" + botName() + "[Bot]</span>";
	}

	public Card chooseBuy(Set<Card> choiceSet) {
		return chooseGainFromSupply(choiceSet, false);
	}

	public Card chooseGainFromSupply(Set<Card> choiceSet, boolean isMandatory) {
		// choose the first priority card that is available
		List<Card> priority = gainPriority();
		for (Card card : priority) {
			if (choiceSet.contains(card)) {
				return card;
			}
		}
		// none of the priority cards are available
		if (isMandatory) {
			return choiceSet.iterator().next();
		} else {
			return null;
		}
	}

	public List<Card> gainPriority() {
		// based on WanderingWinder's "Big Money" bot
		List<Card> priority = new ArrayList<Card>();
		if (game.supply.containsKey(Card.COLONY)) {
			if (getTotalMoney() > 32) {
				priority.add(Card.COLONY);
			}
			if (gainsToEndGame() <= 6) {
				priority.add(Card.PROVINCE);
			}
			if (gainsToEndGame() <= 5) {
				priority.add(Card.DUCHY);
			}
			if (gainsToEndGame() <= 2) {
				priority.add(Card.ESTATE);
			}
			priority.add(Card.PLATINUM);
			if (countInSupply(Card.COLONY) <= 7) {
				priority.add(Card.PROVINCE);
			}
			priority.add(Card.GOLD);
			if (gainsToEndGame() <= 6) {
				priority.add(Card.DUCHY);
			}
			priority.add(Card.SILVER);
			if (gainsToEndGame() <= 2) {
				priority.add(Card.COPPER);
			}
		} else {
			if (getTotalMoney() > 18) {
				priority.add(Card.PROVINCE);
			}
			if (gainsToEndGame() <= 4) {
				priority.add(Card.DUCHY);
			}
			if (gainsToEndGame() <= 2) {
				priority.add(Card.ESTATE);
			}
			priority.add(Card.GOLD);
			if (gainsToEndGame() <= 6) {
				priority.add(Card.DUCHY);
			}
			priority.add(Card.SILVER);
		}
		return priority;
	}

	protected int getTotalMoney() {
		int totalMoney = 0;
		for (Card card : getDeck()) {
			if (card.isTreasure) {
				totalMoney += card.treasureValue(game);
			}
		}
		return totalMoney;
	}

	protected int gainsToEndGame() {
		// get the number of cards in the smallest 3 piles
		List<Integer> piles = new ArrayList<Integer>();
		for (Entry<Card, Integer> pile : game.supply.entrySet()) {
			piles.add(pile.getValue());
		}
		Collections.sort(piles);
		int gains = 0;
		for (int i = 0; i < 3; i++) {
			gains += piles.get(i);
		}
		// compare that to the number of provinces and colonies remaining
		gains = Math.min(gains, game.supply.get(Card.PROVINCE));
		if (game.supply.containsKey(Card.COLONY)) {
			gains = Math.min(gains, game.supply.get(Card.COLONY));
		}
		return gains;
	}

	protected int countInSupply(Card card) {
		if (!game.supply.containsKey(card)) {
			return 0;
		} else {
			return game.supply.get(card);
		}
	}

	protected int countInDeck(Card card) {
		int count = 0;
		for (Card cardInDeck : getDeck()) {
			if (cardInDeck == card) {
				count++;
			}
		}
		return count;
	}

	public Set<Card> required() {
		// BigMoney requires no non-basic cards
		return new HashSet<Card>();
	}

	public Card chooseOpponentGainFromSupply(Set<Card> choiceSet) {
		if (choiceSet.contains(Card.CURSE)) {
			return Card.CURSE;
		} else {
			return choiceSet.iterator().next();
		}
	}

	public Card choosePlay(Set<Card> choiceSet, boolean isMandatory) {
		// play "free" cantrips first (cards that always give at least +1 card, +1 action)
		Card freeCantrip = firstFreeCantrip(choiceSet);
		if (freeCantrip != null) {
			return freeCantrip;
		}
		// play any card that might have some benefit
		for (Card card : choiceSet) {
			if (!hasNoBenefit(card)) {
				return card;
			}
		}
		// if mandatory, return something
		if (isMandatory) {
			return choiceSet.iterator().next();
		} else {
			// otherwise, do nothing
			return null;
		}
	}

	public Card firstFreeCantrip(Set<Card> choiceSet) {
		for (Card card : choiceSet) {
			if (isFreeCantrip(card)) {
				return card;
			}
		}
		return null;
	}

	private boolean isFreeCantrip(Card card) {
		Card[] freeCantrips = new Card[] {Card.VILLAGE, Card.SPY, Card.FESTIVAL, Card.LABORATORY, Card.MARKET,
				Card.GREAT_HALL, Card.WISHING_WELL, Card.MINING_VILLAGE,
				Card.PEARL_DIVER, Card.CARAVAN, Card.BAZAAR, Card.TREASURY,
				Card.WORKERS_VILLAGE, Card.CITY, Card.GRAND_MARKET, Card.PEDDLER};
		for (Card freeCantrip : freeCantrips) {
			if (card == freeCantrip) {
				return true;
			}
		}
		// conspirator is a free cantrip if it will be the third action played (or more)
		if (card == Card.CONSPIRATOR && this.game.actionsPlayedThisTurn >= 2) {
			return true;
		}
		return false;
	}

	private boolean hasNoBenefit(Card card) {
		// if playing this card will get conspirator to become a cantrip, then that is a benefit
		if (getActions() - this.game.actionsPlayedThisTurn >= 3 && getHand().contains(Card.CONSPIRATOR)) {
			return false;
		}
		if (card == Card.CELLAR) {
			// Cellar has no benefit if you do not want to discard anything
			return chooseToDiscard(getHand()) == null;
		} else if (card == Card.CHAPEL) {
			// Chapel has no benefit if you do not want to trash anything
			return chooseToTrash(new HashSet<Card>(getHand())) == null;
		} else if (card == Card.MONEYLENDER) {
			// Moneylender has no benefit if you have no copper
			return !getHand().contains(Card.COPPER);
		} else if (card == Card.THRONE_ROOM) {
			// Throne Room has no benefit if you have no other actions
			int numActions = 0;
			for (Card cardInHand : getHand()) {
				if (cardInHand.isAction) {
					numActions++;
				}
			}
			return numActions >= 2;
		} else if (card == Card.LIBRARY) {
			// Library has no benefit if you already have more than 7 cards in hand
			return getHand().size() > 7;
		} else if (card == Card.MINE) {
			// Mine has no benefit if you have no treasure cards in hand
			for (Card cardInHand : getHand()) {
				if (cardInHand.isTreasure) {
					return false;
				}
			}
			return true;
		} else if (card == Card.COPPERSMITH) {
			// Coppersmith has no benefit if you have no Copper in hand
			return !getHand().contains(Card.COPPER);
		} else if (card == Card.TRADING_POST) {
			// Trading Post has no benefit if you cannot trash two cards from your hand (after playing it)
			return getHand().size() < 3;
		} else if (card == Card.AMBASSADOR) {
			// Ambassador has no benefit if you do not want to trash anything
			return chooseToTrash(new HashSet<Card>(getHand())) == null;
		} else if (card == Card.SMUGGLERS) {
			// Smugglers has no benefit if you can't smuggle anything you want
			Smugglers SMUGGLERS = (Smugglers) Card.SMUGGLERS;
			return chooseGainFromSupply(SMUGGLERS.smuggleable(this, game), false) == null;
		} else if (card == Card.TREASURE_MAP) {
			// Treasure map has no benefit if you don't have another treasure map to trash
			int numTreasureMaps = 0;
			for (Card cardInHand : getHand()) {
				if (cardInHand == Card.TREASURE_MAP) {
					numTreasureMaps++;
				}
			}
			return numTreasureMaps < 2;
		} else if (card == Card.OUTPOST) {
			// Outpost has no benefit if you are already getting an extra turn, or if this is an extra turn
			return hasExtraTurn() || isTakingExtraTurn();
		} else if (card == Card.TACTICIAN) {
			// Tactician has no benefit if you have no other cards to discard
			return getHand().size() == 1;
		} else if (card == Card.WATCHTOWER) {
			// Watchtower has no benefit if you have 6 or more cards in hand (after playing it)
			return getHand().size() > 6;
		} else if (card == Card.COUNTING_HOUSE) {
			// Counting house has no benefit if you have no copper in your discard
			return !getDiscard().contains(Card.COPPER);
		} else if (card == Card.MINT) {
			// Mint has no benefit if you have no treasure in your hand (exactly like Mine)
			return hasNoBenefit(Card.MINE);
		} else if (card == Card.KINGS_COURT) {
			// King's Court has no benefit if you have no other actions (exactly like Throne Room)
			return hasNoBenefit(Card.THRONE_ROOM);
		}
		return false;
	}

	public Card chooseTrashFromHand(Set<Card> choiceSet, boolean isMandatory) {
		// trash a card willingly
		Card card = chooseToTrash(choiceSet);
		if (card != null) {
			return card;
		} else if (!isMandatory) {
			return null;
		}
		// trash the cheapest card
		List<Card> choiceList = new ArrayList<Card>(choiceSet);
		Collections.sort(choiceList, COST_ORDER_COMPARATOR);
		Collections.reverse(choiceList);
		return choiceList.get(0);
	}

	public Card chooseIslandFromHand(Set<Card> choiceSet) {
		// island any plain victory card
		for (Card card : choiceSet) {
			if (isPlainVictory(card)) {
				return card;
			}
		}
		// island the cheapest card
		List<Card> choiceList = new ArrayList<Card>(choiceSet);
		Collections.sort(choiceList, COST_ORDER_COMPARATOR);
		Collections.reverse(choiceList);
		return choiceList.get(0);
	}

	private boolean isPlainVictory(Card card) {
		return card.isVictory && !card.isAction && !card.isVictory;
	}

	public Card choosePutOnDeck(Set<Card> choiceSet) {
		return choiceSet.iterator().next();
	}

	public Card choosePassToOpponent(Set<Card> choiceSet) {
		return chooseTrashFromHand(choiceSet, true);
	}

	public Card chooseRevealAttackReaction(Set<Card> choiceSet) {
		if (choiceSet.contains(Card.MOAT)) {
			return Card.MOAT;
		} else {
			return null;
		}
	}

	public List<Card> discardNumber(int number, boolean isMandatory) {
		List<Card> toDiscard = new ArrayList<Card>();
		List<Card> handCopy = new ArrayList<Card>(getHand());
		// discard some cards willingly
		while (toDiscard.size() < number && chooseToDiscard(handCopy) != null) {
			Card card = chooseToDiscard(handCopy);
			handCopy.remove(card);
			toDiscard.add(card);
		}
		// only discard more if it's mandatory
		if (isMandatory) {
			// discard the cheapest card
			Collections.sort(handCopy, COST_ORDER_COMPARATOR);
			Collections.reverse(handCopy);
			toDiscard.addAll(handCopy.subList(0, number - toDiscard.size()));
		}
		return toDiscard;
	}

	public Card chooseToDiscard(List<Card> cards) {
		for (Card card : cards) {
			if (wantToDiscard(card)) {
				return card;
			}
		}
		return null;
	}

	public boolean wantToDiscard(Card card) {
		return card == Card.CURSE || card.isVictory;
	}

	public List<Card> trashNumber(int number, boolean isMandatory) {
		List<Card> toTrash = new ArrayList<Card>();
		List<Card> handCopy = new ArrayList<Card>(getHand());
		// trash some cards willingly
		while (toTrash.size() < number && chooseToTrash(new HashSet<Card>(handCopy)) != null) {
			Card card = chooseToTrash(new HashSet<Card>(handCopy));
			handCopy.remove(card);
			toTrash.add(card);
		}
		// only trash more if it's mandatory
		if (isMandatory) {
			// trash the cheapest card
			Collections.sort(handCopy, COST_ORDER_COMPARATOR);
			Collections.reverse(handCopy);
			toTrash.addAll(handCopy.subList(0, number - toTrash.size()));
		}
		return toTrash;
	}

	public Card chooseToTrash(Set<Card> choiceSet) {
		for (Card card : trashPriority()) {
			if (choiceSet.contains(card)) {
				return card;
			}
		}
		return null;
	}

	public boolean wantToTrash(Card card) {
		return trashPriority().contains(card);
	}

	public List<Card> trashPriority() {
		List<Card> priority = new ArrayList<Card>();
		priority.add(Card.CURSE);
		if (gainsToEndGame() > 4) {
			priority.add(Card.ESTATE);
		}
		if (getTotalMoney() > 4) {
			priority.add(Card.COPPER);
		}
		return priority;
	}

	public List<Card> putNumberOnDeck(int number) {
		// TODO doesn't really have a clear strategy yet
		return new ArrayList<Card>(getHand().subList(0, Math.min(number, getHand().size())));
	}

	public int multipleChoice(String[] choices, int[] disabledIndexes) {
		for (int i = 0; i < choices.length; i++) {
			boolean disabled = false;
			if (disabledIndexes != null) {
				for (int j = 0; j < disabledIndexes.length; j++) {
					if (disabledIndexes[j] == i) {
						disabled = true;
						break;
					}
				}
			}
			if (!disabled) {
				return i;
			}
		}
		throw new IllegalStateException();
	}

	// card-specific decisions

	public int ambassadorNumToReturn(Card revealed, int maximum) {
		if (wantToTrash(revealed)) {
			return maximum;
		} else {
			return 0;
		}
	}

	public Card hamletDiscardForAction() {
		int numActions = 0;
		for (Card card : getHand()) {
			if (card.isAction) {
				numActions++;
			}
		}
		// if you have more action cards than actions
		if (numActions < getActions()) {
			// discard a curse or plain victory card for +1 action
			for (Card card : getHand()) {
				if (card == Card.CURSE || isPlainVictory(card)) {
					return card;
				}
			}
			return null;
		}
		return null;
	}

	public Card hamletDiscardForBuy() {
		// TODO: find identifiable scenario for taking this tradeoff
		return null;
	}

	public boolean tournamentRevealProvince() {
		// revealing a Province is pretty much always advantageous
		return true;
	}

	public boolean youngWitchRevealBane() {
		// always reveal a bane card instead of gaining a Curse
		return true;
	}

	public boolean jesterGainInsteadOfOpponent(Card card) {
		// gain any card that you wouldn't trash
		return !trashPriority().contains(card);
	}

	public int[] trustySteedBenefits() {
		// +2 cards and +$2 is pretty safe for big money
		return new int[] {0, 2};
	}

	public boolean duchessDiscardTopOfDeck(Card card) {
		// discard the top card of your deck if you know would discard it if it were in your hand
		return wantToDiscard(card);
	}

	public boolean duchessGainDuchessOnGainingDuchy() {
		// BigMoney should not gain actions, even free Duchesses
		return false;
	}

	public boolean foolsGoldReveal() {
		// BigMoney should prefer Gold to Fool's Gold
		return true;
	}

	public boolean oracleDiscardSelf(List<Card> cards) {
		// if there are any you want to discard, discard both
		for (Card card : cards) {
			if (wantToDiscard(card)) {
				return true;
			}
		}
		return false;
	}

	public boolean oracleDiscardOppponent(List<Card> cards) {
		// if there are any you wouldn't discard, discard both
		for (Card card : cards) {
			if (!wantToDiscard(card)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean chooseRevealTunnel() {
		// revealing Tunnel is pretty much always advantageous
		return true;
	}

	public boolean jackOfAllTradesDiscardTopOfDeck(Card card) {
		// discard the top card of your deck if you know would discard it if it were in your hand
		return wantToDiscard(card);
	}

	public boolean nobleBrigandTrashGoldOverSilver() {
		// making opponents trash Gold over Silver is better for BigMoney
		return true;
	}

	public boolean spiceMerchantFirstBenefit() {
		// the second benefit, +$2 and +1 buy, is probably better for BigMoney
		return false;
	}

}
