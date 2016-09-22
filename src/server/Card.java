package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cards.*;

public class Card {

	// basic cards
	public static final Card PROVINCE = new Province();
	public static final Card DUCHY = new Duchy();
	public static final Card ESTATE = new Estate();
	public static final Card COPPER = new Copper();
	public static final Card SILVER = new Silver();
	public static final Card GOLD = new Gold();
	public static final Card CURSE = new Curse();

	// kingdom cards

	// base set
	public static final Card ADVENTURER = new Adventurer();
	public static final Card BUREAUCRAT = new Bureaucrat();
	public static final Card CELLAR = new Cellar();
	public static final Card CHAPEL = new Chapel();
	public static final Card CHANCELLOR = new Chancellor();
	public static final Card COUNCIL_ROOM = new CouncilRoom();
	public static final Card FEAST = new Feast();
	public static final Card FESTIVAL = new Festival();
	public static final Card GARDENS = new Gardens();
	public static final Card LABORATORY = new Laboratory();
	public static final Card LIBRARY = new Library();
	public static final Card MARKET = new Market();
	public static final Card MILITIA = new Militia();
	public static final Card MINE = new Mine();
	public static final Card MOAT = new Moat();
	public static final Card MONEYLENDER = new Moneylender();
	public static final Card REMODEL = new Remodel();
	public static final Card SMITHY = new Smithy();
	public static final Card SPY = new Spy();
	public static final Card THIEF = new Thief();
	public static final Card THRONE_ROOM = new ThroneRoom();
	public static final Card VILLAGE = new Village();
	public static final Card WITCH = new Witch();
	public static final Card WOODCUTTER = new Woodcutter();
	public static final Card WORKSHOP = new Workshop();
	// intrigue expansion
	public static final Card BARON = new Baron();
	public static final Card BRIDGE = new Bridge();
	public static final Card CONSPIRATOR = new Conspirator();
	public static final Card COPPERSMITH = new Coppersmith();
	public static final Card COURTYARD = new Courtyard();
	public static final Card DUKE = new Duke();
	public static final Card GREAT_HALL = new GreatHall();
	public static final Card HAREM = new Harem();
	public static final Card IRONWORKS = new Ironworks();
	public static final Card MASQUERADE = new Masquerade();
	public static final Card MINING_VILLAGE = new MiningVillage();
	public static final Card MINION = new Minion();
	public static final Card NOBLES = new Nobles();
	public static final Card PAWN = new Pawn();
	public static final Card SABOTEUR = new Saboteur();
	public static final Card SCOUT = new Scout();
	public static final Card SECRET_CHAMBER = new SecretChamber();
	public static final Card SHANTY_TOWN = new ShantyTown();
	public static final Card STEWARD = new Steward();
	public static final Card SWINDLER = new Swindler();
	public static final Card TORTURER = new Torturer();
	public static final Card TRADING_POST = new TradingPost();
	public static final Card TRIBUTE = new Tribute();
	public static final Card UPGRADE = new Upgrade();
	public static final Card WISHING_WELL = new WishingWell();
	// seaside expansion
	public static final Card EMBARGO = new Embargo();
	public static final Card HAVEN = new Haven();
	public static final Card LIGHTHOUSE = new Lighthouse();
	public static final Card NATIVE_VILLAGE = new NativeVillage();
	public static final Card PEARL_DIVER = new PearlDiver();
	public static final Card AMBASSADOR = new Ambassador();
	public static final Card FISHING_VILLAGE = new FishingVillage();
	public static final Card LOOKOUT = new Lookout();
	public static final Card SMUGGLERS = new Smugglers();
	public static final Card WAREHOUSE = new Warehouse();
	public static final Card CARAVAN = new Caravan();
	public static final Card CUTPURSE = new Cutpurse();
	public static final Card ISLAND = new Island();
	public static final Card NAVIGATOR = new Navigator();
	public static final Card PIRATE_SHIP = new PirateShip();
	public static final Card SALVAGER = new Salvager();
	public static final Card SEA_HAG = new SeaHag();
	public static final Card TREASURE_MAP = new TreasureMap();
	public static final Card BAZAAR = new Bazaar();
	public static final Card EXPLORER = new Explorer();
	public static final Card GHOST_SHIP = new GhostShip();
	public static final Card MERCHANT_SHIP = new MerchantShip();
	public static final Card OUTPOST = new Outpost();
	public static final Card TACTICIAN = new Tactician();
	public static final Card TREASURY = new Treasury();
	public static final Card WHARF = new Wharf();
	// prosperity expansion
	public static final Card LOAN = new Loan();
	public static final Card QUARRY = new Quarry();
	public static final Card TALISMAN = new Talisman();
	public static final Card CONTRABAND = new Contraband();
	public static final Card ROYAL_SEAL = new RoyalSeal();
	public static final Card VENTURE = new Venture();
	// prosperity basic cards
	public static final Card PLATINUM = new Platinum();
	public static final Card COLONY = new Colony();

	public static Map<String, Card> cardsByName;

	public static Set<Card> BASIC_CARDS;
	public static Set<Card> BASE_SET;
	public static Set<Card> INTRIGUE_SET;
	public static Set<Card> SEASIDE_SET;
	public static Set<Card> PROSPERITY_SET;
	public static Set<Card> PROSPERITY_BASIC_CARDS;

	public static Map<String, Set<Card>> setsByName;
	public static List<Set<Card>> setOrder;
	public static List<String> setNames;

	static {
		cardsByName = new HashMap<String, Card>();
		BASIC_CARDS = new HashSet<Card>();
		BASE_SET = new HashSet<Card>();
		INTRIGUE_SET = new HashSet<Card>();
		SEASIDE_SET = new HashSet<Card>();
		PROSPERITY_SET = new HashSet<Card>();
		PROSPERITY_BASIC_CARDS = new HashSet<Card>();
		setsByName = new HashMap<String, Set<Card>>();

		setOrder = new ArrayList<Set<Card>>();
		setNames = new ArrayList<String>();
		setOrder.add(BASE_SET);
		setNames.add("Base");
		setOrder.add(INTRIGUE_SET);
		setNames.add("Intrigue");
		setOrder.add(SEASIDE_SET);
		setNames.add("Seaside");
		setOrder.add(PROSPERITY_SET);
		setNames.add("Prosperity");
		for (int i = 0; i < setOrder.size(); i++) {
			setsByName.put(setNames.get(i), setOrder.get(i));
		}

		// basic cards
		include(PROVINCE, BASIC_CARDS);
		include(DUCHY, BASIC_CARDS);
		include(ESTATE, BASIC_CARDS);
		include(COPPER, BASIC_CARDS);
		include(SILVER, BASIC_CARDS);
		include(GOLD, BASIC_CARDS);
		include(CURSE, BASIC_CARDS);

		// kingdom cards

		// base set
		include(ADVENTURER, BASE_SET);
		include(BUREAUCRAT, BASE_SET);
		include(CELLAR, BASE_SET);
		include(CHAPEL, BASE_SET);
		include(CHANCELLOR, BASE_SET);
		include(COUNCIL_ROOM, BASE_SET);
		include(FEAST, BASE_SET);
		include(FESTIVAL, BASE_SET);
		include(GARDENS, BASE_SET);
		include(LABORATORY, BASE_SET);
		include(LIBRARY, BASE_SET);
		include(MARKET, BASE_SET);
		include(MILITIA, BASE_SET);
		include(MINE, BASE_SET);
		include(MOAT, BASE_SET);
		include(MONEYLENDER, BASE_SET);
		include(REMODEL, BASE_SET);
		include(SMITHY, BASE_SET);
		include(SPY, BASE_SET);
		include(THIEF, BASE_SET);
		include(THRONE_ROOM, BASE_SET);
		include(VILLAGE, BASE_SET);
		include(WITCH, BASE_SET);
		include(WOODCUTTER, BASE_SET);
		include(WORKSHOP, BASE_SET);
		// intrigue expansion
		include(BARON, INTRIGUE_SET);
		include(BRIDGE, INTRIGUE_SET);
		include(CONSPIRATOR, INTRIGUE_SET);
		include(COPPERSMITH, INTRIGUE_SET);
		include(COURTYARD, INTRIGUE_SET);
		include(DUKE, INTRIGUE_SET);
		include(GREAT_HALL, INTRIGUE_SET);
		include(HAREM, INTRIGUE_SET);
		include(MASQUERADE, INTRIGUE_SET);
		include(MINING_VILLAGE, INTRIGUE_SET);
		include(MINION, INTRIGUE_SET);
		include(IRONWORKS, INTRIGUE_SET);
		include(PAWN, INTRIGUE_SET);
		include(NOBLES, INTRIGUE_SET);
		include(SABOTEUR, INTRIGUE_SET);
		include(SCOUT, INTRIGUE_SET);
		include(SECRET_CHAMBER, INTRIGUE_SET);
		include(SHANTY_TOWN, INTRIGUE_SET);
		include(STEWARD, INTRIGUE_SET);
		include(SWINDLER, INTRIGUE_SET);
		include(TORTURER, INTRIGUE_SET);
		include(TRADING_POST, INTRIGUE_SET);
		include(TRIBUTE, INTRIGUE_SET);
		include(UPGRADE, INTRIGUE_SET);
		include(WISHING_WELL, INTRIGUE_SET);
		// seaside expansion
		include(EMBARGO, SEASIDE_SET);
		include(NATIVE_VILLAGE, SEASIDE_SET);
		include(PEARL_DIVER, SEASIDE_SET);
		include(HAVEN, SEASIDE_SET);
		include(LIGHTHOUSE, SEASIDE_SET);
		include(AMBASSADOR, SEASIDE_SET);
		include(FISHING_VILLAGE, SEASIDE_SET);
		include(LOOKOUT, SEASIDE_SET);
		include(SMUGGLERS, SEASIDE_SET);
		include(WAREHOUSE, SEASIDE_SET);
		include(CARAVAN, SEASIDE_SET);
		include(CUTPURSE, SEASIDE_SET);
		include(ISLAND, SEASIDE_SET);
		include(NAVIGATOR, SEASIDE_SET);
		include(PIRATE_SHIP, SEASIDE_SET);
		include(SALVAGER, SEASIDE_SET);
		include(SEA_HAG, SEASIDE_SET);
		include(TREASURE_MAP, SEASIDE_SET);
		include(BAZAAR, SEASIDE_SET);
		include(EXPLORER, SEASIDE_SET);
		include(GHOST_SHIP, SEASIDE_SET);
		include(MERCHANT_SHIP, SEASIDE_SET);
		include(OUTPOST, SEASIDE_SET);
		include(TACTICIAN, SEASIDE_SET);
		include(TREASURY, SEASIDE_SET);
		include(WHARF, SEASIDE_SET);
		// prosperity expansion
		include(LOAN, PROSPERITY_SET);
		include(QUARRY, PROSPERITY_SET);
		include(TALISMAN, PROSPERITY_SET);
		include(CONTRABAND, PROSPERITY_SET);
		include(ROYAL_SEAL, PROSPERITY_SET);
		include(VENTURE, PROSPERITY_SET);
		// prosperity basic cards
		include(PLATINUM, PROSPERITY_BASIC_CARDS);
		include(COLONY, PROSPERITY_BASIC_CARDS);
	}

	public static void include(Card card, Set<Card> kingdomSet) {
		cardsByName.put(card.toString(), card);
		if (kingdomSet != null) {
			kingdomSet.add(card);
		}
	}

	public static Card fromName(String name) {
		return cardsByName.get(name);
	}

	public boolean isAction;
	public boolean isTreasure;
	public boolean isVictory;
	public boolean isAttack;
	public boolean isAttackReaction;
	public boolean isDuration;

	public int startingSupply(int numPlayers) {
		if (isVictory) {
			return numPlayers == 2 ? 8 : 12;
		} else {
			return 10;
		}
	}

	public int cost(Game game) {
		int computedCost = cost();
		computedCost -=  game.cardCostReduction;
		if (isAction) {
			computedCost -= 2 * game.numberInPlay(Card.QUARRY);
		}
		// cost can never be less than zero
		return Math.max(computedCost, 0);
	}
	public int cost() {
		throw new UnsupportedOperationException();
	}

	public int treasureValue(Game game) {
		return treasureValue();
	}
	public int treasureValue() {
		throw new UnsupportedOperationException();
	}

	public int victoryValue(List<Card> deck) {
		return victoryValue();
	}
	public int victoryValue() {
		throw new UnsupportedOperationException();
	}

	public void onPlay(Player player, Game game) {
		// do nothing (this should only be called by basic treasures)
	}
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		onPlay(player, game);
		return false;
	}

	public void onAttack(Player player, Game game, List<Player> targets) {
		throw new UnsupportedOperationException();
	}
	public boolean onAttackReaction(Player player, Game game) {
		return false;
	}

	public boolean onDurationPlay(Player player, Game game, List<Card> toHaven) {
		onPlay(player, game);
		return true;
	}
	public void onDurationEffect(Player player, Game game, Duration duration) {
		throw new UnsupportedOperationException();
	}

	protected void plusCards(Player player, Game game, int numCards) {
		List<Card> drawn = player.drawIntoHand(numCards);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
	}
	protected void plusActions(Player player, Game game, int numActions) {
		player.addActions(numActions);
		game.messageAll("getting +" + numActions + ((numActions == 1) ? " action" : " actions"));
	}
	protected void plusBuys(Player player, Game game, int numBuys) {
		player.addBuys(numBuys);
		game.messageAll("getting +" + numBuys + ((numBuys == 1) ? " buy" : " buys"));
	}
	protected void plusCoins(Player player, Game game, int numCoins) {
		player.addCoins(numCoins);
		game.messageAll("getting +$" + numCoins);
	}

	public String htmlClass() {
		if (isAction && isVictory) {
			return "action-victory";
		} else if (isTreasure && isVictory) {
			return "treasure-victory";
		} else if (isAttackReaction) {
			return "reaction";
		} else if (isDuration) {
			return "duration";
		} else if (isAction) {
			return "action";
		} else if (isTreasure) {
			return "treasure";
		} else if (isVictory) {
			return "victory";
		} else {
			return "curse";
		}
	}
	private String indefiniteArticle() {
		char firstLetter = this.toString().toLowerCase().charAt(0);
		return isVowel(firstLetter) ? "an" : "a";
	}
	private static char[] vowels = new char[] {'a', 'e', 'i', 'o', 'u'};
	private static boolean isVowel(char letter) {
		for (int i = 0; i < vowels.length; i++) {
			if (letter == vowels[i]) {
				return true;
			}
		}
		return false;
	}
	public String plural() {
		return this.toString() + "s";
	}
	public String htmlName(int count) {
		return ((count > 1 || count == 0) ? count : indefiniteArticle()) + " <span class=\"" + htmlClass() + "\">" + ((count > 1 || count == 0) ? plural() : toString()) + "</span>";
	}
	public String htmlName() {
		return htmlName(1);
	}
	public String htmlNameRaw() {
		return "<span class=\"" + htmlClass() + "\">" + toString() + "</span>";
	}
	public static String htmlList(List<Card> cards) {
		if (cards.size() == 0) {
			return "0 cards";
		}
		Map<Card, Integer> counts = new HashMap<Card, Integer>();
		for (Card card : cards) {
			if (!counts.containsKey(card)) {
				counts.put(card, 1);
			} else {
				counts.put(card, counts.get(card) + 1);
			}
		}
		List<Card> order = new ArrayList<Card>(counts.keySet());
		Collections.sort(order, Player.HAND_ORDER_COMPARATOR);
		// construct comma separated list
		StringBuilder builder = new StringBuilder();
		Iterator<Card> iter = order.iterator();
		while (iter.hasNext()) {
			Card card = iter.next();
			builder.append(card.htmlName(counts.get(card)));
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
	public static String htmlSet(Set<Card> cards) {
		if (cards.size() == 0) {
			return "";
		}
		// construct comma separated list
		StringBuilder builder = new StringBuilder();
		Iterator<Card> iter = cards.iterator();
		while (iter.hasNext()) {
			Card card = iter.next();
			builder.append(card.htmlNameRaw());
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
	public static String numCards(int count) {
		return count + ((count > 1 || count == 0) ? " cards" : " card");
	}

	public String htmlType() {
		if (isAction && isVictory) {
			return "Action-Victory";
		} else if (isTreasure && isVictory) {
			return "Treasure-Victory";
		} else if (isAttackReaction) {
			return "Action-Reaction";
		} else if (isAction) {
			if (isAttack) {
				return "Action-Attack";
			} else if (isDuration) {
				return "Action-Duration";
			} else {
				return "Action";
			}
		} else if (isTreasure) {
			return "Treasure";
		} else if (isVictory) {
			return "Victory";
		} else {
			return "Curse";
		}
	}

	public String[] description() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Card && this.toString().equals(other.toString());
	}

}
