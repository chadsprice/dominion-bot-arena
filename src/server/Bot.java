package server;

import java.util.*;
import java.util.stream.Collectors;

import bots.*;
import cards.Hermit;
import org.json.simple.JSONObject;

import cards.Smugglers;

public class Bot extends Player {

	static Map<String, Class<? extends Bot>> botsByName = new HashMap<>();

	static void initializeBots() {
		include(Bot.class);
		include(BankWharf.class);
		include(BigNothing.class);
		include(BigSmithy.class);
		include(BmLibrary.class);
		include(BmMasquerade.class);
		include(ChapelWitch.class);
		include(DoubleJack.class);
		include(AdvisorBM.class);
		include(Mimic.class);
	}

	private static void include(Class<? extends Bot> botClass) {
		try {
			botsByName.put(botClass.newInstance().botName(), botClass);
		} catch (IllegalAccessException|InstantiationException e) {
			throw new IllegalStateException();
		}
	}

	static Bot newBotFromName(String botName) {
		if (botsByName.containsKey(botName)) {
			try {
				return botsByName.get(botName).newInstance();
			} catch (IllegalAccessException|InstantiationException e) {
				throw new IllegalStateException();
			}
		} else {
			// when given an unknown bot, default to BigMoney
			return new Bot();
		}
	}

	private static final Comparator<Card> COST_ORDER_COMPARATOR =
			// order by highest cost
			(a, b) -> b.cost() - a.cost();

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

	private void setName() {
		username = "<span class=\"botName\">" + botName() + "[Bot]</span>";
	}

	Card chooseBuy(Set<Card> choiceSet) {
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
		List<Card> priority = new ArrayList<>();
		if (game.supply.containsKey(Cards.COLONY)) {
			if (getTotalMoney() > 32) {
				priority.add(Cards.COLONY);
			}
			if (gainsToEndGame() <= 6) {
				priority.add(Cards.PROVINCE);
			}
			if (gainsToEndGame() <= 5) {
				priority.add(Cards.DUCHY);
			}
			if (gainsToEndGame() <= 2) {
				priority.add(Cards.ESTATE);
			}
			priority.add(Cards.PLATINUM);
			if (countInSupply(Cards.COLONY) <= 7) {
				priority.add(Cards.PROVINCE);
			}
			priority.add(Cards.GOLD);
			if (gainsToEndGame() <= 6) {
				priority.add(Cards.DUCHY);
			}
			priority.add(Cards.SILVER);
			if (gainsToEndGame() <= 2) {
				priority.add(Cards.COPPER);
			}
		} else {
			if (getTotalMoney() > 18) {
				priority.add(Cards.PROVINCE);
			}
			if (gainsToEndGame() <= 4) {
				priority.add(Cards.DUCHY);
			}
			if (gainsToEndGame() <= 2) {
				priority.add(Cards.ESTATE);
			}
			priority.add(Cards.GOLD);
			if (gainsToEndGame() <= 6) {
				priority.add(Cards.DUCHY);
			}
			priority.add(Cards.SILVER);
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
		List<Integer> piles = new ArrayList<>();
		piles.addAll(game.supply.values());
		game.mixedPiles.values().stream()
				.map(List::size)
				.forEach(piles::add);
		Collections.sort(piles);
		int gains = 0;
		for (int i = 0; i < 3; i++) {
			gains += piles.get(i);
		}
		// compare that to the number of provinces and colonies remaining
		gains = Math.min(gains, game.supply.get(Cards.PROVINCE));
		if (game.supply.containsKey(Cards.COLONY)) {
			gains = Math.min(gains, game.supply.get(Cards.COLONY));
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
		return Collections.emptySet();
	}

	Card chooseOpponentGainFromSupply(Set<Card> choiceSet) {
		if (choiceSet.contains(Cards.CURSE)) {
			return Cards.CURSE;
		} else {
			return choiceSet.iterator().next();
		}
	}

	Card choosePlay(Set<Card> choiceSet, boolean isMandatory) {
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

	private Card firstFreeCantrip(Set<Card> choiceSet) {
		for (Card card : choiceSet) {
			if (isFreeCantrip(card)) {
				return card;
			}
		}
		return null;
	}

	private boolean isFreeCantrip(Card card) {
		Card[] freeCantrips = new Card[] {Cards.VILLAGE, Cards.SPY, Cards.FESTIVAL, Cards.LABORATORY, Cards.MARKET,
				Cards.GREAT_HALL, Cards.WISHING_WELL, Cards.MINING_VILLAGE,
				Cards.PEARL_DIVER, Cards.CARAVAN, Cards.BAZAAR, Cards.TREASURY,
				Cards.WORKERS_VILLAGE, Cards.CITY, Cards.GRAND_MARKET, Cards.PEDDLER};
		for (Card freeCantrip : freeCantrips) {
			if (card == freeCantrip) {
				return true;
			}
		}
		// conspirator is a free cantrip if it will be the third action played (or more)
		return card == Cards.CONSPIRATOR && this.game.actionsPlayedThisTurn >= 2;
	}

	private boolean hasNoBenefit(Card card) {
		// if playing this card will get conspirator to become a cantrip, then that is a benefit
		if (getActions() - this.game.actionsPlayedThisTurn >= 3 && getHand().contains(Cards.CONSPIRATOR)) {
			return false;
		}
		if (card == Cards.CELLAR) {
			// Cellar has no benefit if you do not want to discard anything
			return chooseToDiscard(getHand()) == null;
		} else if (card == Cards.CHAPEL) {
			// Chapel has no benefit if you do not want to trash anything
			return chooseToTrash(new HashSet<>(getHand())) == null;
		} else if (card == Cards.MONEYLENDER) {
			// Moneylender has no benefit if you have no copper
			return !getHand().contains(Cards.COPPER);
		} else if (card == Cards.THRONE_ROOM) {
			// Throne Room has no benefit if you have no other actions
			int numActions = 0;
			for (Card cardInHand : getHand()) {
				if (cardInHand.isAction) {
					numActions++;
				}
			}
			return numActions >= 2;
		} else if (card == Cards.LIBRARY) {
			// Library has no benefit if you already have more than 7 cards in hand
			return getHand().size() > 7;
		} else if (card == Cards.MINE) {
			// Mine has no benefit if you have no treasure cards in hand
			for (Card cardInHand : getHand()) {
				if (cardInHand.isTreasure) {
					return false;
				}
			}
			return true;
		} else if (card == Cards.COPPERSMITH) {
			// Coppersmith has no benefit if you have no Copper in hand
			return !getHand().contains(Cards.COPPER);
		} else if (card == Cards.TRADING_POST) {
			// Trading Post has no benefit if you cannot trash two cards from your hand (after playing it)
			return getHand().size() < 3;
		} else if (card == Cards.AMBASSADOR) {
			// Ambassador has no benefit if you do not want to trash anything
			return chooseToTrash(new HashSet<>(getHand())) == null;
		} else if (card == Cards.SMUGGLERS) {
			// Smugglers has no benefit if you can't smuggle anything you want
			Smugglers SMUGGLERS = (Smugglers) Cards.SMUGGLERS;
			return chooseGainFromSupply(SMUGGLERS.smuggleable(this, game), false) == null;
		} else if (card == Cards.TREASURE_MAP) {
			// Treasure map has no benefit if you don't have another treasure map to trash
			int numTreasureMaps = 0;
			for (Card cardInHand : getHand()) {
				if (cardInHand == Cards.TREASURE_MAP) {
					numTreasureMaps++;
				}
			}
			return numTreasureMaps < 2;
		} else if (card == Cards.OUTPOST) {
			// Outpost has no benefit if you are already getting an extra turn, or if this is an extra turn
			return hasExtraTurn() || isTakingExtraTurn();
		} else if (card == Cards.TACTICIAN) {
			// Tactician has no benefit if you have no other cards to discard
			return getHand().size() == 1;
		} else if (card == Cards.WATCHTOWER) {
			// Watchtower has no benefit if you have 6 or more cards in hand (after playing it)
			return getHand().size() > 6;
		} else if (card == Cards.COUNTING_HOUSE) {
			// Counting house has no benefit if you have no copper in your discard
			return !getDiscard().contains(Cards.COPPER);
		} else if (card == Cards.MINT) {
			// Mint has no benefit if you have no treasure in your hand (exactly like Mine)
			return hasNoBenefit(Cards.MINE);
		} else if (card == Cards.KINGS_COURT) {
			// King's Court has no benefit if you have no other actions (exactly like Throne Room)
			return hasNoBenefit(Cards.THRONE_ROOM);
		}
		return false;
	}

	Card chooseTrashFromHand(Set<Card> choiceSet, boolean isMandatory) {
		// trash a card willingly
		Card card = chooseToTrash(choiceSet);
		if (card != null) {
			return card;
		} else if (!isMandatory) {
			return null;
		}
		// trash the cheapest card
		List<Card> choiceList = new ArrayList<>(choiceSet);
		Collections.sort(choiceList, COST_ORDER_COMPARATOR);
		Collections.reverse(choiceList);
		return choiceList.get(0);
	}

	Card chooseIslandFromHand(Set<Card> choiceSet) {
		// island any plain victory card
		for (Card card : choiceSet) {
			if (isPlainVictory(card)) {
				return card;
			}
		}
		// island the cheapest card
		List<Card> choiceList = new ArrayList<>(choiceSet);
		Collections.sort(choiceList, COST_ORDER_COMPARATOR);
		Collections.reverse(choiceList);
		return choiceList.get(0);
	}

	private boolean isPlainVictory(Card card) {
		return card.isVictory && !card.isAction && !card.isTreasure;
	}

	Card choosePutOnDeck(Set<Card> choiceSet) {
		return choiceSet.iterator().next();
	}

	Card choosePassToOpponent(Set<Card> choiceSet) {
		return chooseTrashFromHand(choiceSet, true);
	}

	Card chooseRevealAttackReaction(Set<Card> choiceSet) {
		if (choiceSet.contains(Cards.MOAT)) {
			return Cards.MOAT;
		} else {
			return null;
		}
	}

	List<Card> discardNumber(int number, boolean isMandatory) {
		List<Card> toDiscard = new ArrayList<>();
		List<Card> handCopy = new ArrayList<>(getHand());
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

	private Card chooseToDiscard(List<Card> cards) {
		for (Card card : cards) {
			if (wantToDiscard(card)) {
				return card;
			}
		}
		return null;
	}

	public boolean wantToDiscard(Card card) {
		return card == Cards.CURSE || card.isVictory;
	}

	List<Card> trashNumber(int number, boolean isMandatory) {
		List<Card> toTrash = new ArrayList<>();
		List<Card> handCopy = new ArrayList<>(getHand());
		// trash some cards willingly
		while (toTrash.size() < number && chooseToTrash(new HashSet<>(handCopy)) != null) {
			Card card = chooseToTrash(new HashSet<>(handCopy));
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

	private Card chooseToTrash(Set<Card> choiceSet) {
		for (Card card : trashPriority()) {
			if (choiceSet.contains(card)) {
				return card;
			}
		}
		return null;
	}

	private boolean wantToTrash(Card card) {
		return trashPriority().contains(card);
	}

	public List<Card> trashPriority() {
		List<Card> priority = new ArrayList<>();
		priority.add(Cards.CURSE);
		priority.add(Cards.OVERGROWN_ESTATE);
		if (gainsToEndGame() > 4) {
			priority.add(Cards.ESTATE);
		}
		if (getTotalMoney() > 4) {
			priority.add(Cards.COPPER);
		}
		return priority;
	}

	List<Card> putNumberOnDeck(int number) {
		// TODO doesn't really have a clear strategy yet
		return new ArrayList<>(getHand().subList(0, Math.min(number, getHand().size())));
	}

	int multipleChoice(String[] choices, int[] disabledIndexes) {
		// return the fist non-disabled choices
		for (int i = 0; i < choices.length; i++) {
			// if this choice is not disabled, return it
			if (!isDisabled(i, disabledIndexes)) {
				return i;
			}
		}
		throw new IllegalStateException();
	}

	private static boolean isDisabled(int i, int[] disabledIndexes) {
		return disabledIndexes != null && Arrays.stream(disabledIndexes).anyMatch(disabledIndex -> disabledIndex == i);
	}

	// utility functions

	private Card mostExpensive(Collection<Card> cards) {
        Optional<Card> mostExpensive = cards.stream().max((a, b) -> a.cost(game) - b.cost(game));
        if (mostExpensive.isPresent()) {
            return mostExpensive.get();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private Card leastExpensive(Collection<Card> cards) {
        Optional<Card> leastExpensive = cards.stream().min((a, b) -> a.cost(game) - b.cost(game));
        if (leastExpensive.isPresent()) {
            return leastExpensive.get();
        } else {
            throw new IllegalArgumentException();
        }
    }

	// card-specific decisions

	public Card topTwoCardAttackTrash(Set<Card> trashable) {
		// if there is a card you want to trash
		Optional<Card> optionalToTrash = trashable.stream().filter(this::wantToTrash).findFirst();
		if (optionalToTrash.isPresent()) {
			// trash it
			return optionalToTrash.get();
		} else {
			// otherwise, trash the cheapest
            return leastExpensive(trashable);
		}
	}

	public Card harbingerPutFromDiscardOntoDeck(Set<Card> cards) {
        // put the most expensive non-victory card on top of your deck
        Set<Card> nonVictory = cards.stream().filter(c -> !c.isVictory).collect(Collectors.toSet());
        if (nonVictory.isEmpty()) {
            return mostExpensive(nonVictory);
        } else {
            // if there is none, do nothing
            return null;
        }
	}

	@SuppressWarnings("unused")
	public boolean vassalPlay(Card card) {
        // playing the action is probably beneficial
        return true;
    }

    public boolean moneylenderTrashCopper() {
        // trashing a Copper for +$3 is good for BigMoney
        return true;
    }

	@SuppressWarnings("unused")
    public boolean librarySetAside(Card card) {
        // BigMoney should avoid action cards whenever possible
        return true;
    }

    public Card mineTrash(Set<Card> trashable) {
        // upgrade the most expensive treasure possible
        if (trashable.contains(Cards.GOLD) && game.isAvailableInSupply(Cards.PLATINUM)) {
            return Cards.GOLD;
        } else if (trashable.contains(Cards.SILVER) && game.isAvailableInSupply(Cards.GOLD)) {
            return Cards.SILVER;
        } else if (trashable.contains(Cards.COPPER) && game.isAvailableInSupply(Cards.SILVER)) {
            return Cards.COPPER;
        } else {
            // if there is no basic upgrade available, trash nothing
            return null;
        }
    }

    public Card mineFirstEditionTrash(Set<Card> trashable) {
        // first edition mine is mandatory, so first check if there is a card you would trash even if it weren't mandatory
        Card wantToTrash = mineTrash(trashable);
        if (wantToTrash != null) {
            return wantToTrash;
        } else {
            // if not, just trash something
            return trashable.iterator().next();
        }
    }

    public int sentryTrashDiscardOrPutBack(Card card) {
        if (wantToTrash(card)) {
            return 0;
        } else if (wantToDiscard(card)) {
            return 1;
        } else {
            return 2;
        }
    }

    public boolean chancellorPutDeckIntoDiscard() {
        // putting your deck into your discard speeds up cycling
        return true;
    }

    public Card thiefTrash(Set<Card> trashable) {
        // trash the opponent's most expensive treasure
        return mostExpensive(trashable);
    }

    public boolean thiefGainTrashed(Card trashed) {
        // gain any non-Copper treasure
        return trashed != Cards.COPPER;
    }

    public boolean lurkerTrashOverGain() {
        Set<Card> gainable = game.getTrash().stream()
                .filter(c -> c.isAction)
                .collect(Collectors.toSet());
        // if there is no card you want to gain from the trash, choose to trash a card from the supply instead
        return chooseGainFromSupply(gainable, false) == null;
    }

    public Card lurkerTrashFromSupply(Set<Card> trashable) {
        // trash a card that you want to later gain
        return chooseGainFromSupply(trashable, true);
    }

    public int[] pawnBenefits() {
        // +1 Card and +$1 is a safe choice for BigMoney
        return new int[] {0, 3};
    }

	public int stewardBenefit() {
		// if there are two cards in hand you want to trash, trash them
		if (getHand().stream().filter(this::wantToTrash).count() >= 2) {
			return 2;
		} else {
			// otherwise, for BigMoney, +2 Cards is better on average than +$2
			return 0;
		}
	}

	public boolean baronDiscardEstate() {
		// discard an Estate for +$4
		return true;
	}

	public boolean miningVillageTrash() {
		// trashing an action card for coin is beneficial for BigMoney
		return true;
	}

	public Card courtierReveal(Set<Card> revealable) {
		// reveal the card with the most types
		Optional<Card> mostTypes = revealable.stream().max(
				(a, b) -> a.htmlType().split("-").length - b.htmlType().split("-").length);
		if (mostTypes.isPresent()) {
			return mostTypes.get();
		} else {
			// revealable must be empty, so no choice can be made
			throw new IllegalArgumentException();
		}
	}

	public List<Integer> courtierBenefits(int numBenefits) {
		// in order of usefulness for BigMoney: +$3, gain Gold, +1 Buy, +1 Action
		return Arrays.asList(new Integer[] {2, 3, 1, 0}).subList(0, numBenefits);
	}

	public boolean minionCoinOverAttack() {
		// take the $2 if you have another Minion in hand to chain into
		return getHand().contains(Cards.MINION);
	}

	public boolean torturerDiscardTwoOverTakingCurse() {
		// if you have enough cards that you want to discard, discard them
		return getHand().stream().filter(this::wantToDiscard).count() >= Math.min(2, getHand().size());
	}

	public boolean noblesCardsOverActions() {
		// +3 Cards is better than +2 Actions for BigMoney
		return true;
	}

	@SuppressWarnings("unused")
	public Object embargoPile(Set<Card> cardPiles, Set<Card.MixedPileId> mixedPiles) {
		// embargo something random (no clear strategy yet)
		return cardPiles.iterator().next();
	}

	public Card havenSetAside(Set<Card> canSetAside) {
		// set aside something random (no clear strategy yet)
		return canSetAside.iterator().next();
	}

	public int ambassadorNumToReturn(Card revealed, int maximum) {
		if (wantToTrash(revealed)) {
			return maximum;
		} else {
			return 0;
		}
	}

	public int treasuryNumToPutOnDeck(int max) {
		// always put your Treasuries on top of your deck when you can
		return max;
	}

	public boolean loanDiscardOverTrash(Card card) {
		// discard the revealed treasure if you don't want to trash it
		return !wantToTrash(card);
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
				if (card == Cards.CURSE || isPlainVictory(card)) {
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

	boolean duchessGainDuchessOnGainingDuchy() {
		// BigMoney should not gain actions, even free Duchesses
		return false;
	}

	boolean foolsGoldReveal() {
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

	boolean traderReplaceWithSilver(Card card) {
		// if it is a card that you would trash, gain a Silver instead
		return wantToTrash(card);
	}

	public List<Card> cartographerDiscardAnyNumber(List<Card> cards) {
		// discard any that you would discard if they were in your hand
		return cards.stream().filter(this::wantToDiscard).collect(Collectors.toList());
	}

	public boolean illGottenGainsGainCopper() {
		// gaining a Copper is rarely beneficial for BigMoney
		return false;
	}

	@SuppressWarnings("unused")
	public List<Card> innShuffleIntoDeck(List<Card> innable) {
		// BigMoney shouldn't want more actions in its deck
		return new ArrayList<>();
	}

	public Card stablesDiscard(Set<Card> treasures) {
		// discarding a Copper for +3 cards is probably beneficial for BigMoney
		if (treasures.contains(Cards.COPPER)) {
			return Cards.COPPER;
		} else {
			return null;
		}
	}

	public boolean survivorsDiscard(List<Card> cards) {
		// if you would discard either of them, discard all of them
		for (Card card : cards) {
			if (wantToDiscard(card)) {
				return true;
			}
		}
		return false;
	}

	public int squireBenefit() {
		// gaining a Silver is the safest choice for BigMoney
		return 2;
	}

	public boolean hovelTrash() {
		// trashing a Hovel is advantageous for BigMoney
		return true;
	}

	public boolean marketSquareDiscard() {
		// only discard a market square if your will gain a gold
		return game.supply.get(Cards.GOLD) != 0;
	}

	public boolean ironmongerDiscardTopOfDeck(Card card) {
		// discard the top card of your deck if you know would discard it if it were in your hand
		return wantToDiscard(card);
	}

	public boolean scavengerPutDeckIntoDiscard() {
		// putting your deck into your discard is better for cycling with BigMoney
		return true;
	}

	public Card scavengerPutFromDiscardOntoDeck(Set<Card> cards) {
		// put the most expensive treasure on top of your deck
		List<Card> treasuresByCost = cards.stream().filter(c -> c.isTreasure).collect(Collectors.toList());
		if (!treasuresByCost.isEmpty()) {
			Collections.sort(treasuresByCost, COST_ORDER_COMPARATOR);
			return treasuresByCost.get(0);
		} else {
			// if no treasures in discard, just pick something
			return cards.iterator().next();
		}
	}

	public boolean catacombsPutIntoHand(List<Card> cards) {
		// put the top 3 cards of your deck into your hand if you wouldn't discard any
		return cards.stream().noneMatch(this::wantToDiscard);
	}

	public int countFirstBenefit() {
		// discard 2 cards if you want to (other options aren't great)
		if (getHand().stream().filter(this::wantToDiscard).count() >= 2) {
			return 0;
		} else {
			// otherwise put a card from your hand on top of your deck
			return 1;
		}
	}

	public int countSecondBenefit() {
		// trash your entire hand if you want to (unlikely, but very useful if true)
		if (getHand().stream().allMatch(this::wantToTrash)) {
			return 1;
		} else {
			// otherwise, +$3
			return 0;
		}
	}

	public boolean cultistPlayAnotherCultist() {
		// there is little downside to playing another Cultist
		return true;
	}

	public boolean graverobberGain() {
		// only gain Gold from the trash (unlikely)
		return game.getTrash().stream().anyMatch(c -> c == Cards.GOLD);
	}

	public boolean huntingGroundsGainDuchy() {
		// gain a Duchy if there are any in the supply, otherwise gain 3 Estates
		return game.supply.get(Cards.DUCHY) != 0;
	}

	public Hermit.CardFromDiscardOrHand hermitTrash(Set<Card> trashableFromDiscard, Set<Card> trashableFromHand) {
		// first look for cards to trash in your discard (as cards in hand may still be useful this turn)
		Optional<Card> optionalToTrash = trashableFromDiscard.stream().filter(this::wantToTrash).findFirst();
		if (optionalToTrash.isPresent()) {
			return new Hermit.CardFromDiscardOrHand(optionalToTrash.get(), true);
		}
		// then look in your hand
		optionalToTrash = trashableFromHand.stream().filter(this::wantToTrash).findFirst();
		if (optionalToTrash.isPresent()) {
			return new Hermit.CardFromDiscardOrHand(optionalToTrash.get(), false);
		}
		// if you don't want to trash any, return none
		return new Hermit.CardFromDiscardOrHand(null, false);
	}

	boolean urchinTrashForMercenary() {
		// Mercenary is likely to be better than Urchin
		return true;
	}

	public Card pillageOpponentDiscard(Set<Card> discardable) {
		// have opponent discard their most expensive non-victory card
		List<Card> nonVictory = discardable.stream().filter(c -> !c.isVictory).collect(Collectors.toList());
		if (!nonVictory.isEmpty()) {
			Collections.sort(nonVictory, COST_ORDER_COMPARATOR);
			return nonVictory.get(0);
		} else {
			// otherwise, just pick something
			return discardable.iterator().next();
		}
	}

	public Card bandOfMisfitsImitate(Set<Card> imitable) {
		// imitate the most expensive
		List<Card> imitableByCost = new ArrayList<>(imitable);
		Collections.sort(imitableByCost, COST_ORDER_COMPARATOR);
		return imitableByCost.get(0);
	}

	public int doctorTrashDiscardOrPutBack(Card card) {
		// just do the same thing as Sentry
		return sentryTrashDiscardOrPutBack(card);
	}

	public Card advisorChooseOpponentDiscard(Set<Card> discardable) {
		// have your opponent discard the most expensive non-victory card
		Set<Card> nonVictory = discardable.stream()
				.filter(c -> !c.isVictory)
				.collect(Collectors.toSet());
		if (!nonVictory.isEmpty()) {
			return mostExpensive(nonVictory);
		} else {
			return mostExpensive(discardable);
		}
	}

	public Card plazaDiscard(Set<Card> discardable) {
		// discarding a Copper for a coin token is always beneficial
		if (discardable.contains(Cards.COPPER)) {
			return Cards.COPPER;
		} else {
			return null;
		}
	}

	public Card taxmanTrash(Set<Card> trashable) {
		// Taxman is very similar to Mine, so just use the same logic
		return mineTrash(trashable);
	}

	public Card heraldPutFromDiscardOntoDeck(Set<Card> cards) {
		// put the most expensive non-victory card on top of your deck
		Set<Card> nonVictory = cards.stream()
				.filter(c -> !c.isVictory)
				.collect(Collectors.toSet());
		if (nonVictory.isEmpty()) {
			return mostExpensive(nonVictory);
		} else {
			// if there is none, pick something at random
			return cards.iterator().next();
		}
	}

	@SuppressWarnings("unused")
	public int butcherSpendCoinTokens(int max, Card trashed) {
		// just spend the most you can (no clear strategy yet)
		return max;
	}

}
