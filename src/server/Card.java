package server;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

	// base set (cards in both 1st and 2nd editions)
	public static final Card BUREAUCRAT = new Bureaucrat();
	public static final Card CELLAR = new Cellar();
	public static final Card CHAPEL = new Chapel();
	public static final Card COUNCIL_ROOM = new CouncilRoom();
	public static final Card FESTIVAL = new Festival();
	public static final Card GARDENS = new Gardens();
	public static final Card LABORATORY = new Laboratory();
	public static final Card LIBRARY = new Library();
	public static final Card MARKET = new Market();
	public static final Card MILITIA = new Militia();
	public static final Card MOAT = new Moat();
	public static final Card REMODEL = new Remodel();
	public static final Card SMITHY = new Smithy();
	public static final Card VILLAGE = new Village();
	public static final Card WITCH = new Witch();
	public static final Card WORKSHOP = new Workshop();
	// base set (1st edition only)
	public static final Card ADVENTURER = new Adventurer();
	public static final Card CHANCELLOR = new Chancellor();
	public static final Card FEAST = new Feast();
	public static final Card MINE_FIRST_EDITION = new MineFirstEdition();
	public static final Card MONEYLENDER_FIRST_EDITION = new MoneylenderFirstEdition();
	public static final Card SPY = new Spy();
	public static final Card THIEF = new Thief();
	public static final Card THRONE_ROOM_FIRST_EDITION = new ThroneRoomFirstEdition();
	public static final Card WOODCUTTER = new Woodcutter();
	// base set (2nd edition only)
	public static final Card ARTISAN = new Artisan();
	public static final Card BANDIT = new Bandit();
	public static final Card HARBINGER = new Harbinger();
	public static final Card MERCHANT = new Merchant();
	public static final Card MINE = new Mine();
	public static final Card MONEYLENDER = new Moneylender();
	public static final Card POACHER = new Poacher();
	public static final Card SENTRY = new Sentry();
	public static final Card THRONE_ROOM = new ThroneRoom();
	public static final Card VASSAL = new Vassal();
	// intrigue expansion (cards in both 1st and 2nd editions)
	public static final Card BARON = new Baron();
	public static final Card BRIDGE = new Bridge();
	public static final Card CONSPIRATOR = new Conspirator();
	public static final Card COURTYARD = new Courtyard();
	public static final Card DUKE = new Duke();
	public static final Card HAREM = new Harem();
	public static final Card IRONWORKS = new Ironworks();
	public static final Card MINING_VILLAGE = new MiningVillage();
	public static final Card MINION = new Minion();
	public static final Card NOBLES = new Nobles();
	public static final Card PAWN = new Pawn();
	public static final Card SHANTY_TOWN = new ShantyTown();
	public static final Card STEWARD = new Steward();
	public static final Card SWINDLER = new Swindler();
	public static final Card TORTURER = new Torturer();
	public static final Card TRADING_POST = new TradingPost();
	public static final Card UPGRADE = new Upgrade();
	public static final Card WISHING_WELL = new WishingWell();
	// intrigue expansion (1st edition only)
	public static final Card COPPERSMITH = new Coppersmith();
	public static final Card GREAT_HALL = new GreatHall();
	public static final Card MASQUERADE_FIRST_EDITION = new MasqueradeFirstEdition();
	public static final Card SABOTEUR = new Saboteur();
	public static final Card SCOUT = new Scout();
	public static final Card SECRET_CHAMBER = new SecretChamber();
	public static final Card TRIBUTE = new Tribute();
	// intrigue expansion (2nd edition only)
	public static final Card COURTIER = new Courtier();
	public static final Card DIPLOMAT = new Diplomat();
	public static final Card LURKER = new Lurker();
	public static final Card MASQUERADE = new Masquerade();
	public static final Card MILL = new Mill();
	public static final Card PATROL = new Patrol();
	public static final Card REPLACE = new Replace();
	public static final Card SECRET_PASSAGE = new SecretPassage();
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
	public static final Card TRADE_ROUTE = new TradeRoute();
	public static final Card WATCHTOWER = new Watchtower();
	public static final Card BISHOP = new Bishop();
	public static final Card MONUMENT = new Monument();
	public static final Card QUARRY = new Quarry();
	public static final Card TALISMAN = new Talisman();
	public static final Card WORKERS_VILLAGE = new WorkersVillage();
	public static final Card CITY = new City();
	public static final Card CONTRABAND = new Contraband();
	public static final Card COUNTING_HOUSE = new CountingHouse();
	public static final Card MINT = new Mint();
	public static final Card MOUNTEBANK = new Mountebank();
	public static final Card RABBLE = new Rabble();
	public static final Card ROYAL_SEAL = new RoyalSeal();
	public static final Card VAULT = new Vault();
	public static final Card VENTURE = new Venture();
	public static final Card GOONS = new Goons();
	public static final Card GRAND_MARKET = new GrandMarket();
	public static final Card HOARD = new Hoard();
	public static final Card BANK = new Bank();
	public static final Card EXPAND = new Expand();
	public static final Card FORGE = new Forge();
	public static final Card KINGS_COURT = new KingsCourt();
	public static final Card PEDDLER = new Peddler();
	// prosperity basic cards
	public static final Card PLATINUM = new Platinum();
	public static final Card COLONY = new Colony();
	// cornucopia expansion
	public static final Card HAMLET = new Hamlet();
	public static final Card FORTUNE_TELLER = new FortuneTeller();
	public static final Card MENAGERIE = new Menagerie();
	public static final Card FARMING_VILLAGE = new FarmingVillage();
	public static final Card HORSE_TRADERS = new HorseTraders();
	public static final Card REMAKE = new Remake();
	public static final Card TOURNAMENT = new Tournament();
	public static final Card YOUNG_WITCH = new YoungWitch();
	public static final Card HARVEST = new Harvest();
	public static final Card HORN_OF_PLENTY = new HornOfPlenty();
	public static final Card HUNTING_PARTY = new HuntingParty();
	public static final Card JESTER = new Jester();
	public static final Card FAIRGROUNDS = new Fairgrounds();
	// cornucopia prizes
	public static final Card BAG_OF_GOLD = new BagOfGold();
	public static final Card DIADEM = new Diadem();
	public static final Card FOLLOWERS = new Followers();
	public static final Card PRINCESS = new Princess();
	public static final Card TRUSTY_STEED = new TrustySteed();
	// hinterlands expansion
	public static final Card CROSSROADS = new Crossroads();
	public static final Card DUCHESS = new Duchess();
	public static final Card FOOLS_GOLD = new FoolsGold();
	public static final Card DEVELOP = new Develop();
	public static final Card OASIS = new Oasis();
	public static final Card ORACLE = new Oracle();
	public static final Card SCHEME = new Scheme();
	public static final Card TUNNEL = new Tunnel();
	public static final Card JACK_OF_ALL_TRADES = new JackOfAllTrades();
	public static final Card NOBLE_BRIGAND = new NobleBrigand();
	public static final Card NOMAD_CAMP = new NomadCamp();
	public static final Card SILK_ROAD = new SilkRoad();
	public static final Card SPICE_MERCHANT = new SpiceMerchant();
	public static final Card TRADER = new Trader();
	public static final Card CACHE = new Cache();
	public static final Card CARTOGRAPHER = new Cartographer();
	public static final Card EMBASSY = new Embassy();
	public static final Card HAGGLER = new Haggler();
	public static final Card HIGHWAY = new Highway();
	public static final Card ILL_GOTTEN_GAINS = new IllGottenGains();
	public static final Card INN = new Inn();
	public static final Card MANDARIN = new Mandarin();
	public static final Card MARGRAVE = new Margrave();
	public static final Card STABLES = new Stables();
	public static final Card BORDER_VILLAGE = new BorderVillage();
	public static final Card FARMLAND = new Farmland();
	// dark ages expansion
	public static final Card POOR_HOUSE = new PoorHouse();
	public static final Card BEGGAR = new Beggar();
	public static final Card SQUIRE = new Squire();
	public static final Card VAGRANT = new Vagrant();
	public static final Card FORAGER = new Forager();
	public static final Card HERMIT = new Hermit();
	public static final Card MARKET_SQUARE = new MarketSquare();
	public static final Card SAGE = new Sage();
	public static final Card STOREROOM = new Storeroom();
	public static final Card URCHIN = new Urchin();
	public static final Card ARMORY = new Armory();
	public static final Card DEATH_CART = new DeathCart();
	public static final Card FEODUM = new Feodum();
	public static final Card FORTRESS = new Fortress();
	public static final Card IRONMONGER = new Ironmonger();
	public static final Card MARAUDER = new Marauder();
	public static final Card PROCESSION = new Procession();
	public static final Card RATS = new Rats();
	public static final Card SCAVENGER = new Scavenger();
	public static final Card WANDERING_MINSTREL = new WanderingMinstrel();
    public static final Card BAND_OF_MISFITS = new BandOfMisfits();
	public static final Card BANDIT_CAMP = new BanditCamp();
	public static final Card CATACOMBS = new Catacombs();
	public static final Card COUNT = new Count();
	public static final Card COUNTERFEIT = new Counterfeit();
	public static final Card CULTIST = new Cultist();
	public static final Card GRAVEROBBER = new Graverobber();
	public static final Card JUNK_DEALER = new JunkDealer();
	public static final Card KNIGHTS = new Knights();
	public static final Card MYSTIC = new Mystic();
	public static final Card PILLAGE = new Pillage();
	public static final Card REBUILD = new Rebuild();
	public static final Card ROGUE = new Rogue();
	public static final Card ALTAR = new Altar();
	public static final Card HUNTING_GROUNDS = new HuntingGrounds();
	// dark ages ruins
	public static final Card ABANDONED_MINE = new AbandonedMine();
	public static final Card RUINED_LIBRARY = new RuinedLibrary();
	public static final Card RUINED_MARKET = new RuinedMarket();
	public static final Card RUINED_VILLAGE = new RuinedVillage();
	public static final Card SURVIVORS = new Survivors();
	// dark ages knights
	public static final Card DAME_ANNA = new DameAnna();
	public static final Card DAME_JOSEPHINE = new DameJosephine();
	public static final Card DAME_MOLLY = new DameMolly();
	public static final Card DAME_NATALIE = new DameNatalie();
	public static final Card DAME_SYLVIA = new DameSylvia();
	public static final Card SIR_BAILEY = new SirBailey();
	public static final Card SIR_DESTRY = new SirDestry();
	public static final Card SIR_MARTIN = new SirMartin();
	public static final Card SIR_MICHAEL = new SirMichael();
	public static final Card SIR_VANDER = new SirVander();
	// dark ages non-supply
	public static final Card MADMAN = new Madman();
	public static final Card MERCENARY = new Mercenary();
	public static final Card SPOILS = new Spoils();
	// dark ages shelters
	public static final Card HOVEL = new Hovel();
	public static final Card NECROPOLIS = new Necropolis();
	public static final Card OVERGROWN_ESTATE = new OvergrownEstate();
	// guilds expansion
	public static final Card CANDLESTICK_MAKER = new CandlestickMaker();
	public static final Card STONEMASON = new Stonemason();
	public static final Card DOCTOR = new Doctor();
	public static final Card MASTERPIECE = new Masterpiece();
	public static final Card ADVISOR = new Advisor();
	public static final Card PLAZA = new Plaza();
	public static final Card TAXMAN = new Taxman();
	public static final Card HERALD = new Herald();
	public static final Card BAKER = new Baker();
	public static final Card SOOTHSAYER = new Soothsayer();

	public static Map<String, Card> cardsByName = new HashMap<>();

	static Set<Card> BASIC_CARDS = new HashSet<>();
	static Set<Card> BASE_SET = new HashSet<>();
	static Set<Card> BASE_SET_FIRST_EDITION = new HashSet<>();
	static Set<Card> INTRIGUE_SET = new HashSet<>();
	static Set<Card> INTRIGUE_SET_FIRST_EDITION = new HashSet<>();
	static Set<Card> SEASIDE_SET = new HashSet<>();
	static Set<Card> PROSPERITY_SET = new HashSet<>();
	static Set<Card> PROSPERITY_BASIC_CARDS = new HashSet<>();
	static Set<Card> CORNUCOPIA_SET = new HashSet<>();
	static Set<Card> PRIZE_CARDS = new HashSet<>();
	static Set<Card> HINTERLANDS_SET = new HashSet<>();
	static Set<Card> DARK_AGES_SET = new HashSet<>();
	static Set<Card> RUINS_CARDS = new HashSet<>();
	static Set<Card> KNIGHT_CARDS = new HashSet<>();
	static Set<Card> DARK_AGES_NON_SUPPLY_CARDS = new HashSet<>();
	static Set<Card> SHELTER_CARDS = new HashSet<>();
	static Set<Card> GUILDS_SET = new HashSet<>();

	public static Map<String, Set<Card>> setsByName = new HashMap<>();
	public static List<Set<Card>> setOrder = new ArrayList<>();
	public static List<String> setNames = new ArrayList<>();

	public static void initializeCardSets() {

		include(BASE_SET, "Base");
		include(BASE_SET_FIRST_EDITION, "Base (1st Ed.)");
		include(INTRIGUE_SET, "Intrigue");
		include(INTRIGUE_SET_FIRST_EDITION, "Intrigue (1st Ed.)");
		include(SEASIDE_SET, "Seaside");
		include(PROSPERITY_SET, "Prosperity");
		include(CORNUCOPIA_SET, "Cornucopia");
		include(HINTERLANDS_SET, "Hinterlands");
		include(DARK_AGES_SET, "Dark Ages");
		include(GUILDS_SET, "Guilds");

		// basic cards
		include(PROVINCE, BASIC_CARDS);
		include(DUCHY, BASIC_CARDS);
		include(ESTATE, BASIC_CARDS);
		include(COPPER, BASIC_CARDS);
		include(SILVER, BASIC_CARDS);
		include(GOLD, BASIC_CARDS);
		include(CURSE, BASIC_CARDS);

		// kingdom cards

		// base set (cards in both 1st and 2nd editions)
		include(BUREAUCRAT, BASE_SET);
		include(CELLAR, BASE_SET);
		include(CHAPEL, BASE_SET);
		include(COUNCIL_ROOM, BASE_SET);
		include(FESTIVAL, BASE_SET);
		include(GARDENS, BASE_SET);
		include(LABORATORY, BASE_SET);
		include(LIBRARY, BASE_SET);
		include(MARKET, BASE_SET);
		include(MILITIA, BASE_SET);
		include(MOAT, BASE_SET);
		include(REMODEL, BASE_SET);
		include(SMITHY, BASE_SET);
		include(VILLAGE, BASE_SET);
		include(WITCH, BASE_SET);
		include(WOODCUTTER, BASE_SET);
		include(WORKSHOP, BASE_SET);
		BASE_SET_FIRST_EDITION.addAll(BASE_SET);
		// base set (1st edition only)
		include(ADVENTURER, BASE_SET_FIRST_EDITION);
		include(CHANCELLOR, BASE_SET_FIRST_EDITION);
		include(FEAST, BASE_SET_FIRST_EDITION);
		include(MINE_FIRST_EDITION, BASE_SET_FIRST_EDITION);
		include(MONEYLENDER_FIRST_EDITION, BASE_SET_FIRST_EDITION);
		include(SPY, BASE_SET_FIRST_EDITION);
		include(THIEF, BASE_SET_FIRST_EDITION);
		include(THRONE_ROOM_FIRST_EDITION, BASE_SET_FIRST_EDITION);
		include(WOODCUTTER, BASE_SET_FIRST_EDITION);
		// base set (2nd edition only)
		include(ARTISAN, BASE_SET);
		include(BANDIT, BASE_SET);
		include(HARBINGER, BASE_SET);
		include(MERCHANT, BASE_SET);
		include(MINE, BASE_SET);
		include(MONEYLENDER, BASE_SET);
		include(POACHER, BASE_SET);
		include(SENTRY, BASE_SET);
		include(THRONE_ROOM, BASE_SET);
		include(VASSAL, BASE_SET);
		// intrigue expansion (cards in both 1st and 2nd editions)
		include(BARON, INTRIGUE_SET);
		include(BRIDGE, INTRIGUE_SET);
		include(CONSPIRATOR, INTRIGUE_SET);
		include(COURTYARD, INTRIGUE_SET);
		include(DUKE, INTRIGUE_SET);
		include(HAREM, INTRIGUE_SET);
		include(MINING_VILLAGE, INTRIGUE_SET);
		include(MINION, INTRIGUE_SET);
		include(IRONWORKS, INTRIGUE_SET);
		include(PAWN, INTRIGUE_SET);
		include(NOBLES, INTRIGUE_SET);
		include(SHANTY_TOWN, INTRIGUE_SET);
		include(STEWARD, INTRIGUE_SET);
		include(SWINDLER, INTRIGUE_SET);
		include(TORTURER, INTRIGUE_SET);
		include(TRADING_POST, INTRIGUE_SET);
		include(UPGRADE, INTRIGUE_SET);
		include(WISHING_WELL, INTRIGUE_SET);
		INTRIGUE_SET_FIRST_EDITION.addAll(INTRIGUE_SET);
		// intrigue expansion (1st edition only)
		include(COPPERSMITH, INTRIGUE_SET_FIRST_EDITION);
		include(GREAT_HALL, INTRIGUE_SET_FIRST_EDITION);
		include(MASQUERADE_FIRST_EDITION, INTRIGUE_SET_FIRST_EDITION);
		include(SABOTEUR, INTRIGUE_SET_FIRST_EDITION);
		include(SCOUT, INTRIGUE_SET_FIRST_EDITION);
		include(SECRET_CHAMBER, INTRIGUE_SET_FIRST_EDITION);
		include(TRIBUTE, INTRIGUE_SET_FIRST_EDITION);
		// intrigue expansion (2nd edition only)
		include(COURTIER, INTRIGUE_SET);
		include(DIPLOMAT, INTRIGUE_SET);
		include(LURKER, INTRIGUE_SET);
		include(MASQUERADE, INTRIGUE_SET);
		include(MILL, INTRIGUE_SET);
		include(PATROL, INTRIGUE_SET);
		include(REPLACE, INTRIGUE_SET);
		include(SECRET_PASSAGE, INTRIGUE_SET);
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
		include(TRADE_ROUTE, PROSPERITY_SET);
		include(WATCHTOWER, PROSPERITY_SET);
		include(BISHOP, PROSPERITY_SET);
		include(MONUMENT, PROSPERITY_SET);
		include(QUARRY, PROSPERITY_SET);
		include(TALISMAN, PROSPERITY_SET);
		include(WORKERS_VILLAGE, PROSPERITY_SET);
		include(CITY, PROSPERITY_SET);
		include(CONTRABAND, PROSPERITY_SET);
		include(COUNTING_HOUSE, PROSPERITY_SET);
		include(MINT, PROSPERITY_SET);
		include(MOUNTEBANK, PROSPERITY_SET);
		include(RABBLE, PROSPERITY_SET);
		include(ROYAL_SEAL, PROSPERITY_SET);
		include(VAULT, PROSPERITY_SET);
		include(VENTURE, PROSPERITY_SET);
		include(GOONS, PROSPERITY_SET);
		include(GRAND_MARKET, PROSPERITY_SET);
		include(HOARD, PROSPERITY_SET);
		include(BANK, PROSPERITY_SET);
		include(EXPAND, PROSPERITY_SET);
		include(FORGE, PROSPERITY_SET);
		include(KINGS_COURT, PROSPERITY_SET);
		include(PEDDLER, PROSPERITY_SET);
		// prosperity basic cards
		include(PLATINUM, PROSPERITY_BASIC_CARDS);
		include(COLONY, PROSPERITY_BASIC_CARDS);
		// cornucopia expansion
		include(HAMLET, CORNUCOPIA_SET);
		include(FORTUNE_TELLER, CORNUCOPIA_SET);
		include(MENAGERIE, CORNUCOPIA_SET);
		include(FARMING_VILLAGE, CORNUCOPIA_SET);
		include(HORSE_TRADERS, CORNUCOPIA_SET);
		include(REMAKE, CORNUCOPIA_SET);
		include(TOURNAMENT, CORNUCOPIA_SET);
		include(YOUNG_WITCH, CORNUCOPIA_SET);
		include(HARVEST, CORNUCOPIA_SET);
		include(HORN_OF_PLENTY, CORNUCOPIA_SET);
		include(HUNTING_PARTY, CORNUCOPIA_SET);
		include(JESTER, CORNUCOPIA_SET);
		include(FAIRGROUNDS, CORNUCOPIA_SET);
		// cornucopia prizes
		include(BAG_OF_GOLD, PRIZE_CARDS);
		include(DIADEM, PRIZE_CARDS);
		include(FOLLOWERS, PRIZE_CARDS);
		include(PRINCESS, PRIZE_CARDS);
		include(TRUSTY_STEED, PRIZE_CARDS);
		// hinterlands expansion
		include(CROSSROADS, HINTERLANDS_SET);
		include(DUCHESS, HINTERLANDS_SET);
		include(FOOLS_GOLD, HINTERLANDS_SET);
		include(DEVELOP, HINTERLANDS_SET);
		include(OASIS, HINTERLANDS_SET);
		include(ORACLE, HINTERLANDS_SET);
		include(SCHEME, HINTERLANDS_SET);
		include(TUNNEL, HINTERLANDS_SET);
		include(JACK_OF_ALL_TRADES, HINTERLANDS_SET);
		include(NOBLE_BRIGAND, HINTERLANDS_SET);
		include(NOMAD_CAMP, HINTERLANDS_SET);
		include(SILK_ROAD, HINTERLANDS_SET);
		include(SPICE_MERCHANT, HINTERLANDS_SET);
		include(TRADER, HINTERLANDS_SET);
		include(CACHE, HINTERLANDS_SET);
		include(CARTOGRAPHER, HINTERLANDS_SET);
		include(EMBASSY, HINTERLANDS_SET);
		include(HAGGLER, HINTERLANDS_SET);
		include(HIGHWAY, HINTERLANDS_SET);
		include(ILL_GOTTEN_GAINS, HINTERLANDS_SET);
		include(INN, HINTERLANDS_SET);
		include(MANDARIN, HINTERLANDS_SET);
		include(MARGRAVE, HINTERLANDS_SET);
		include(STABLES, HINTERLANDS_SET);
		include(BORDER_VILLAGE, HINTERLANDS_SET);
		include(FARMLAND, HINTERLANDS_SET);
		// dark ages expansion
		include(POOR_HOUSE, DARK_AGES_SET);
		include(BEGGAR, DARK_AGES_SET);
		include(SQUIRE, DARK_AGES_SET);
		include(VAGRANT, DARK_AGES_SET);
		include(FORAGER, DARK_AGES_SET);
		include(HERMIT, DARK_AGES_SET);
		include(MARKET_SQUARE, DARK_AGES_SET);
		include(SAGE, DARK_AGES_SET);
		include(STOREROOM, DARK_AGES_SET);
		include(URCHIN, DARK_AGES_SET);
		include(ARMORY, DARK_AGES_SET);
		include(DEATH_CART, DARK_AGES_SET);
		include(FEODUM, DARK_AGES_SET);
		include(FORTRESS, DARK_AGES_SET);
		include(IRONMONGER, DARK_AGES_SET);
		include(MARAUDER, DARK_AGES_SET);
		include(PROCESSION, DARK_AGES_SET);
		include(RATS, DARK_AGES_SET);
		include(SCAVENGER, DARK_AGES_SET);
		include(BANDIT_CAMP, DARK_AGES_SET);
		include(WANDERING_MINSTREL, DARK_AGES_SET);
        include(BAND_OF_MISFITS, DARK_AGES_SET);
		include(CATACOMBS, DARK_AGES_SET);
		include(COUNT, DARK_AGES_SET);
		include(COUNTERFEIT, DARK_AGES_SET);
		include(CULTIST, DARK_AGES_SET);
		include(GRAVEROBBER, DARK_AGES_SET);
		include(JUNK_DEALER, DARK_AGES_SET);
		include(KNIGHTS, DARK_AGES_SET);
		include(MYSTIC, DARK_AGES_SET);
		include(PILLAGE, DARK_AGES_SET);
		include(REBUILD, DARK_AGES_SET);
		include(ROGUE, DARK_AGES_SET);
		include(ALTAR, DARK_AGES_SET);
		include(HUNTING_GROUNDS, DARK_AGES_SET);
		// dark ages ruins
		include(ABANDONED_MINE, RUINS_CARDS);
		include(RUINED_LIBRARY, RUINS_CARDS);
		include(RUINED_MARKET, RUINS_CARDS);
		include(RUINED_VILLAGE, RUINS_CARDS);
		include(SURVIVORS, RUINS_CARDS);
		// dark ages knights
		include(DAME_ANNA, KNIGHT_CARDS);
		include(DAME_JOSEPHINE, KNIGHT_CARDS);
		include(DAME_MOLLY, KNIGHT_CARDS);
		include(DAME_NATALIE, KNIGHT_CARDS);
		include(DAME_SYLVIA, KNIGHT_CARDS);
		include(SIR_BAILEY, KNIGHT_CARDS);
		include(SIR_DESTRY, KNIGHT_CARDS);
		include(SIR_MARTIN, KNIGHT_CARDS);
		include(SIR_MICHAEL, KNIGHT_CARDS);
		include(SIR_VANDER, KNIGHT_CARDS);
		// dark ages non-supply
		include(MADMAN, DARK_AGES_NON_SUPPLY_CARDS);
		include(MERCENARY, DARK_AGES_NON_SUPPLY_CARDS);
		include(SPOILS, DARK_AGES_NON_SUPPLY_CARDS);
		// dark ages shelters
		include(HOVEL, SHELTER_CARDS);
		include(NECROPOLIS, SHELTER_CARDS);
		include(OVERGROWN_ESTATE, SHELTER_CARDS);
		// guilds expansion
		include(CANDLESTICK_MAKER, GUILDS_SET);
		include(STONEMASON, GUILDS_SET);
		include(DOCTOR, GUILDS_SET);
		include(MASTERPIECE, GUILDS_SET);
		include(ADVISOR, GUILDS_SET);
		include(PLAZA, GUILDS_SET);
		include(TAXMAN, GUILDS_SET);
		include(HERALD, GUILDS_SET);
		include(BAKER, GUILDS_SET);
		include(SOOTHSAYER, GUILDS_SET);
	}

	public static void include(Set<Card> cardSet, String name) {
		setOrder.add(cardSet);
		setNames.add(name);
		setsByName.put(name, cardSet);
	}

	public static void include(Card card, Set<Card> kingdomSet) {
		cardsByName.put(card.toString(), card);
		// include simplified name user-typed cards to ignore capitalization and punctuation
		cardsByName.put(simplifiedName(card.toString()), card);
		if (kingdomSet != null) {
			kingdomSet.add(card);
		}
	}

	public static String simplifiedName(String name) {
		return name.toLowerCase().replaceAll("[^a-z]", "");
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
	public boolean isRuins;
	public boolean isShelter;
	public boolean isLooter;
	public boolean isOverpayable;

    public boolean isBandOfMisfits;

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
			computedCost -= 2 * game.numberInPlay(QUARRY);
		}
		computedCost -= 2 * game.numberInPlay(PRINCESS);
		computedCost -= game.numberInPlay(HIGHWAY);
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
	public boolean onAttack(Player player, Game game, List<Player> targets, boolean hasMoved) {
		onAttack(player, game, targets);
		return false;
	}

	public boolean onAttackReaction(Player player, Game game) {
		return false;
	}

	public boolean onDurationPlay(Player player, Game game, List<Card> toHaven) {
		onPlay(player, game);
		return true;
	}
	public void onDurationEffect(Player player, Game game) {
		throw new UnsupportedOperationException();
	}
	public void onDurationEffect(Player player, Game game, DurationEffect duration) {
		onDurationEffect(player, game);
	}

	public void onGain(Player player, Game game) {}

    public void onTrash(Player player, Game game) {}
	public boolean onTrashIsTrashed(Player player, Game game) {
        onTrash(player, game);
        return true;
    }

    public void onOverpay(Player player, Game game, int amountOverpaid) {}

    // common modular effects

	protected void plusCards(Player player, Game game, int numCards) {
        if (numCards == 0) {
            return;
        }
        List<Card> drawn = player.drawIntoHand(numCards);
        game.message(player, "drawing " + Card.htmlList(drawn));
        game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
	}
	protected void plusActions(Player player, Game game, int numActions) {
		if (numActions == 0) {
			return;
		}
		game.messageAll("getting +" + numActions + ((numActions == 1) ? " action" : " actions"));
		player.addActions(numActions);
	}
	protected void plusBuys(Player player, Game game, int numBuys) {
		if (numBuys == 0) {
			return;
		}
		game.messageAll("getting +" + numBuys + ((numBuys == 1) ? " buy" : " buys"));
		player.addBuys(numBuys);
	}
	protected void plusCoins(Player player, Game game, int numCoins) {
		if (numCoins == 0) {
			return;
		}
		game.messageAll("getting +$" + numCoins);
		player.addCoins(numCoins);
	}
	protected void plusCoinTokens(Player player, Game game, int numTokens) {
		if (numTokens == 0) {
			return;
		}
		game.messageAll("getting +" + numTokens + " coin " + (numTokens == 1 ? "token" : "tokens"));
		player.addCoinTokens(numTokens);
	}
	protected void plusVictoryTokens(Player player, Game game, int numTokens) {
		if (numTokens == 0) {
			return;
		}
		game.messageAll("getting +" + numTokens + " VP");
		player.addVictoryTokens(numTokens);
	}

    protected void gain(Player player, Game game, Card card) {
        if (game.supply.get(card) != 0) {
            game.messageAll("gaining " + card.htmlName());
            game.gain(player, card);
        }
    }

    protected void gain(Player player, Game game, Card card, int number) {
		int numGainable = Math.min(number, game.supply.get(card));
		game.messageAll("gaining " + card.htmlName(numGainable));
		for (int i = 0; i < numGainable && game.supply.get(card) != 0; i++) {
			game.gain(player, card);
		}
	}

	protected void gainOntoDeck(Player player, Game game, Card card) {
        if (game.supply.get(card) != 0) {
            game.message(player, "gaining " + card.htmlName() + " onto your deck");
            game.messageOpponents(player, "gaining " + card.htmlName() + " onto their deck");
            game.gainToTopOfDeck(player, card);
        }
    }

    protected void discardNumber(Player player, Game game, int number) {
        if (player.getHand().isEmpty() || number == 0) {
            return;
        }
        List<Card> toDiscard = game.promptDiscardNumber(player, number, this.toString());
        game.messageAll("discarding " + Card.htmlList(toDiscard));
        player.putFromHandIntoDiscard(toDiscard);
    }

	protected List<Card> discardAnyNumber(Player player, Game game) {
		if (player.getHand().isEmpty()) {
			return new ArrayList<>();
		}
        List<Card> discarded = game.promptDiscardNumber(player, player.getHand().size(), false, this.toString());
        game.messageAll("discarding " + Card.htmlList(discarded));
        player.putFromHandIntoDiscard(discarded);
        return discarded;
    }

    protected Card gainCardCostingUpTo(Player player, Game game, int cost) {
        Set<Card> gainable = game.cardsCostingAtMost(cost);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain.");
            game.messageAll("gaining " + toGain.htmlName());
            boolean replaced = game.gain(player, toGain);
			if (!replaced) {
				return toGain;
			}
        } else {
            game.messageAll("gaining nothing");
        }
        return null;
    }

    protected void handSizeAttack(List<Player> targets, Game game, int handSize) {
        targets.forEach(target -> {
            if (target.getHand().size() > handSize) {
                int numToDiscard = target.getHand().size() - handSize;
                List<Card> discarded = game.promptDiscardNumber(target, numToDiscard, this.toString());
                game.message(target, "You discard " + Card.htmlList(discarded));
                game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
                target.putFromHandIntoDiscard(discarded);
            }
        });
    }

    protected void junkingAttack(List<Player> targets, Game game, Card junk) {
        targets.forEach(target -> {
            if (game.isAvailableInSupply(junk)) {
                game.message(target, "You gain " + junk.htmlName());
                game.messageOpponents(target, target.username + " gains " + junk.htmlName());
                game.gain(target, junk);
            }
        });
    }

    protected void topTwoCardsAttack(List<Player> targets, Game game, Predicate<Card> trashablePredicate) {
        targets.forEach(target -> {
            // draw 2 cards
            List<Card> drawn = target.takeFromDraw(2);
            if (!drawn.isEmpty()) {
                // announce the drawn cards
                game.message(target, "You draw " + Card.htmlList(drawn));
                game.messageOpponents(target, target.username + " draws " + Card.htmlList(drawn));
                game.messageIndent++;
                // filter out those that can be trashed
                Set<Card> trashable = drawn.stream().filter(trashablePredicate).collect(Collectors.toSet());
                if (!trashable.isEmpty()) {
                    // choose one to trash
                    Card toTrash;
                    if (trashable.size() == 1) {
                        toTrash = trashable.iterator().next();
                    } else {
                        toTrash = topTwoCardsAttackChooseTrash(target, game, trashable);
                    }
                    // trash it
                    game.messageAll("trashing the " + toTrash.htmlNameRaw());
                    drawn.remove(toTrash);
                    game.trash(target, toTrash);
                }
                if (!drawn.isEmpty()) {
                    game.messageAll("discarding the rest");
                    target.addToDiscard(drawn);
                }
                game.messageIndent--;
            } else {
                game.message(target, "Your deck is empty");
                game.messageOpponents(target, target.username + "'s deck is empty");
            }
        });
    }
    private Card topTwoCardsAttackChooseTrash(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).topTwoCardAttackTrash(trashable);
            if (!trashable.contains(toTrash)) {
                throw new IllegalStateException();
            }
            return toTrash;
        }
        return game.promptMultipleChoiceCard(player, this.toString() + ": You draw " + Card.htmlList(new ArrayList<>(trashable)) + ". Choose one to trash.", "attackPrompt", trashable);
    }

    protected Card topCardOfDeck(Player player) {
        List<Card> drawn = player.takeFromDraw(1);
        if (!drawn.isEmpty()) {
            return drawn.get(0);
        } else {
            return null;
        }
    }

    protected void onRemodelVariant(Player player, Game game, int coins, boolean isExact) {
        if (!player.getHand().isEmpty()) {
            // trash a card from your hand
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()), this.toString() + ": Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain a card from the supply costing more
            Set<Card> gainable;
            if (isExact) {
                gainable = game.cardsCostingExactly(toTrash.cost(game) + coins);
            } else {
                gainable = game.cardsCostingAtMost(toTrash.cost(game) + coins);
            }
            if (!gainable.isEmpty()) {
                Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain.");
                game.messageAll("gaining " + toGain.htmlName());
				game.gain(player, toGain);
            } else {
                game.messageAll("gaining nothing");
            }
        } else {
            game.messageAll("having no card to trash");
        }
    }

	protected boolean onThroneRoomVariant(Player player, Game game, int multiplier, boolean isMandatory, boolean hasMoved) {
        boolean usedAsModifier = false;
        Set<Card> actions = player.getHand().stream()
                .filter(c -> c.isAction)
                .collect(Collectors.toSet());
        if (!actions.isEmpty()) {
            Card toPlay;
            if (isMandatory) {
                toPlay = game.promptChoosePlay(player, actions,
                        this.toString() + ": Choose an action from your hand to play " + (multiplier == 2 ? "twice" : "three times") + ".");
            } else {
                toPlay = game.promptChoosePlay(player, actions,
                        this.toString() + ": You may play an action from your hand " + (multiplier == 2 ? "twice" : "three times") + ".",
                        false, "None");
            }
            if (toPlay != null) {
                game.messageAll("choosing " + toPlay.htmlName());
                // put the chosen card into play
                player.putFromHandIntoPlay(toPlay);
                // handle Band of Misfits entirely separately
                if (toPlay == Card.BAND_OF_MISFITS) {
                    return onThroneRoomVariantBandOfMisfits(player, game, multiplier, hasMoved);
                }
                // play it multiple times
                boolean toPlayMoved = false;
                for (int i = 0; i < multiplier; i++) {
                    toPlayMoved |= game.playAction(player, toPlay, toPlayMoved);
                    // if the card was a duration card, and it was set aside, and this hasn't been moved
                    if (toPlay.isDuration && toPlayMoved && !hasMoved && !usedAsModifier) {
                        // set this aside as a modifier
                        player.removeFromPlay(this);
                        player.addDurationSetAside(this);
                        usedAsModifier = true;
                    }
                }
                afterThroneRoomVariant(player, game, toPlay, toPlayMoved);
            } else {
                game.messageAll("choosing nothing");
            }
        } else {
            game.messageAll("having no actions");
        }
        return usedAsModifier;
    }
    private boolean onThroneRoomVariantBandOfMisfits(Player player, Game game, int multiplier, boolean hasMoved) {
        boolean usedAsModifier = false;
        Set<Card> imitable = game.cardsCostingAtMost(Card.BAND_OF_MISFITS.cost(game) - 1).stream()
                .filter(c -> c.isAction && c != this)
                .collect(Collectors.toSet());
        if (!imitable.isEmpty()) {
            Card toImitate = BandOfMisfits.chooseImitate(player, game, imitable);
            // replace this in play with an imitator
            Card imitator;
            try {
                imitator = toImitate.getClass().newInstance();
                imitator.isBandOfMisfits = true;
            } catch (Exception e) {
                throw new IllegalStateException();
            }
            player.removeFromPlay(this);
            player.addToPlay(imitator);
            // play it multiple times
            boolean imitatorMoved = false;
            for (int i = 0; i < multiplier; i++) {
                imitatorMoved |= game.playAction(player, imitator, imitatorMoved);
                // if the card was a duration card, and it was set aside, and this hasn't been moved
                if (imitator.isDuration && imitatorMoved && !hasMoved && !usedAsModifier) {
                    // set this aside as a modifier
                    player.removeFromPlay(this);
                    player.addDurationSetAside(this);
                    usedAsModifier = true;
                }
            }
        } else {
            game.messageAll("there are no cards it can be played as");
        }
        return usedAsModifier;
    }
    protected void afterThroneRoomVariant(Player player, Game game, Card played, boolean playedMoved) {}

	protected void onMasqueradeVariant(Player player, Game game, boolean isSecondEdition) {
        plusCards(player, game, 2);
        // ask players in turn order which card they want to pass, starting with this player
        List<Player> passOrder = game.getOpponents(player);
        passOrder.add(0, player);
        if (isSecondEdition) {
            // skip over players with no cards in hand
            passOrder = passOrder.stream()
                    .filter(p -> !p.getHand().isEmpty())
                    .collect(Collectors.toList());
        }
        // only bother to pass cards if there are at least 2 players that will pass cards
        if (passOrder.size() >= 2) {
            // choose all cards to pass first
            List<Card> cardsToPass = new ArrayList<>();
            int i = 0;
            for (Player passingPlayer : passOrder) {
                Player receivingPlayer = passOrder.get((i + 1) % passOrder.size());
                String promptType = (passingPlayer == player) ? "actionPrompt" : "attackPrompt";
                Card toPass = game.promptChoosePassToOpponent(passingPlayer, new HashSet<>(passingPlayer.getHand()),
                        this.toString() + ": Pass a card from your hand to " + receivingPlayer.username + ".", promptType);
                cardsToPass.add(toPass);
                i++;
            }
            // then actually pass them
            for (i = 0; i < passOrder.size(); i++) {
                Player passingPlayer = passOrder.get(i);
                Player receivingPlayer = passOrder.get((i + 1) % passOrder.size());
                Card toPass = cardsToPass.get(i);
                if (toPass != null) {
                    passingPlayer.removeFromHand(toPass);
                    receivingPlayer.addToHand(toPass);
                }
                String cardString = (toPass != null) ? toPass.htmlName() : "nothing";
                // message player who is passing
                game.message(passingPlayer, "You pass " + cardString + " to " + receivingPlayer.username);
                // message player who is receiving
                game.message(receivingPlayer, passingPlayer.username + " passes " + cardString + " to you");
                // message other players (without naming the card passed)
                cardString = (toPass != null) ? "a card" : "nothing";
                for (Player uninvolvedPlayer : passOrder) {
                    if (uninvolvedPlayer != passingPlayer && uninvolvedPlayer != receivingPlayer) {
                        game.message(uninvolvedPlayer, passingPlayer.username + " passes " + cardString + " to " + receivingPlayer.username);
                    }
                }
            }
        }
        // you may trash a card from your hand
        if (player.getHand().size() != 0){
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()),
                    this.toString() + ": You may trash a card from your hand",
                    false, "Trash nothing");
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
            }
        }
    }

	protected void putOnDeckInAnyOrder(Player player, Game game, List<Card> cards, String prompt) {
		List<Card> toPutOnDeck;
		// if there is no order to decide
		if (new HashSet<Card>(cards).size() < 2) {
			// put them on the deck as-is
			toPutOnDeck = cards;
		} else {
			// otherwise, prompt the user for the order
			toPutOnDeck = new ArrayList<Card>();
			Collections.sort(cards, Player.HAND_ORDER_COMPARATOR);
			while (!cards.isEmpty()) {
				String[] choices = new String[cards.size()];
				for (int i = 0; i < cards.size(); i++) {
					choices[i] = cards.get(i).toString();
				}
				int choice = game.promptMultipleChoice(player, prompt + " (the first card you choose will be on top of your deck)", choices);
				toPutOnDeck.add(cards.remove(choice));
			}
		}
		if (!toPutOnDeck.isEmpty()) {
			player.putOnDraw(toPutOnDeck);
		}
	}

	protected void revealHand(Player player, Game game) {
		if (!player.getHand().isEmpty()) {
			game.message(player, "revealing your hand: " + Card.htmlList(player.getHand()));
			game.messageOpponents(player, "revealing their hand: " + Card.htmlList(player.getHand()));
		} else {
			game.messageAll("revealing an empty hand");
		}
	}

	protected void revealUntil(Player player, Game game, Predicate<Card> condition, Consumer<Card> onFound) {
		revealUntil(player, game, condition, onFound, false);
	}

	protected void revealUntil(Player player, Game game, Predicate<Card> condition, Consumer<Card> onFound, boolean isTarget) {
        revealUntil(player, game, condition, 1, list -> onFound.accept(list.get(0)), isTarget);
	}

    protected void revealUntil(Player player, Game game, Predicate<Card> condition, int numToFind, Consumer<List<Card>> onFound) {
        revealUntil(player, game, condition, numToFind, onFound, false);
    }

    protected void revealUntil(Player player, Game game, Predicate<Card> condition, int numToFind, Consumer<List<Card>> onFound, boolean isTarget) {
        List<Card> revealed = new ArrayList<>();
        List<Card> found = new ArrayList<>();
        for (;;) {
            List<Card> drawn = player.takeFromDraw(1);
            if (drawn.isEmpty()) {
                break;
            }
            Card card = drawn.get(0);
            revealed.add(card);
            if (condition.test(card)) {
                found.add(card);
                if (found.size() == numToFind) {
                    break;
                }
            }
        }
        if (!revealed.isEmpty()) {
            if (isTarget) {
                game.message(player, "You draw " + Card.htmlList(revealed));
                game.messageOpponents(player, player.username + " draws " + Card.htmlList(revealed));
                game.messageIndent++;
            } else {
                game.messageAll("drawing " + Card.htmlList(revealed));
            }
            if (!found.isEmpty()) {
                found.forEach(revealed::remove);
                onFound.accept(found);
            }
            if (!revealed.isEmpty()) {
                game.messageAll("discarding the rest");
                player.addToDiscard(revealed);
            }
            if (isTarget) {
                game.messageIndent--;
            }
        } else {
            if (!isTarget) {
                game.message(player, "your deck is empty");
                game.messageOpponents(player, "their deck is empty");
            } else {
                game.message(player, "Your deck is empty");
                game.messageOpponents(player, player.username + "'s deck is empty");
            }
        }
    }

	protected void putRevealedIntoHand(Player player, Game game, Card card) {
		game.message(player, "putting the " + card.htmlNameRaw() + " into your hand");
		game.messageOpponents(player, "putting the " + card.htmlNameRaw() + " into their hand");
		player.addToHand(card);
	}

    protected void putRevealedIntoHand(Player player, Game game, List<Card> cards) {
        game.message(player, "putting " + Card.htmlList(cards) + " into your hand");
        game.messageOpponents(player, "putting " + Card.htmlList(cards) + " into their hand");
        player.addToHand(cards);
    }

	protected void putRevealedOnDeck(Player player, Game game, Card card) {
		game.messageAll("putting the " + card.htmlNameRaw() + " back on top");
		player.putOnDraw(card);
	}

	protected void gainFromTrashSatisfying(Player player, Game game, Predicate<Card> predicate, String promptMessage) {
		gainFromTrashSatisfying(player, game, predicate, promptMessage, false);
	}

	protected void gainFromTrashSatisfying(Player player, Game game, Predicate<Card> predicate, String promptMessage, boolean toDeck) {
		Set<Card> gainable = game.getTrash().stream().filter(predicate).collect(Collectors.toSet());
		if (!gainable.isEmpty()) {
			Card toGain = chooseGainFromTrash(player, game, gainable, promptMessage);
			game.messageAll("gaining " + toGain.htmlName() + " from the trash");
			game.gainFromTrash(player, toGain, toDeck);
		} else {
			game.messageAll("gaining nothing");
		}
	}

	protected Card chooseGainFromTrash(Player player, Game game, Set<Card> gainable, String promptMessage) {
		if (player instanceof Bot) {
			Card toGain = ((Bot) player).chooseGainFromSupply(gainable, true);
			if (!gainable.contains(toGain)) {
				throw new IllegalStateException();
			}
			return toGain;
		}
		List<Card> gainableSorted = new ArrayList<>(gainable);
		Collections.sort(gainableSorted, Player.HAND_ORDER_COMPARATOR);
		String[] choices = new String[gainableSorted.size()];
		for (int i = 0; i < gainableSorted.size(); i++) {
			choices[i] = gainableSorted.get(i).toString();
		}
		int choice = game.promptMultipleChoice(player, promptMessage, choices);
		return gainableSorted.get(choice);
	}

	protected void tryToNameTopCardOfDeck(Player player, Game game) {
		Card namedCard = game.promptNameACard(player, this.toString(), "Name a card. If that is the top card of your deck, it will go into your hand");
		List<Card> drawn = player.takeFromDraw(1);
		if (!drawn.isEmpty()) {
			Card revealedCard = drawn.get(0);
			if (namedCard == revealedCard) {
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it into your hand");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it into their hand");
				player.addToHand(revealedCard);
			} else {
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it back");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it back");
				player.putOnDraw(revealedCard);
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
	}

	protected void putACardFromYourHandOntoYourDeck(Player player, Game game) {
        if (!player.getHand().isEmpty()) {
            Card toPutOnDeck = game.promptChoosePutOnDeck(player, new HashSet<>(player.getHand()), this.toString() + ": Put a card from your hand onto your deck.");
            game.message(player, "putting " + toPutOnDeck.htmlName() + " from your hand onto your deck");
            game.messageOpponents(player, "putting a card from their hand onto their deck");
            player.putFromHandOntoDraw(toPutOnDeck);
        }
    }

    protected int[] chooseTwoDifferentBenefits(Player player, Game game, String[] benefits) {
        int firstChoice = game.promptMultipleChoice(player, this.toString() + ": Choose two (the choices must be different)", benefits);
        int secondChoice = game.promptMultipleChoice(player, this.toString() + ": Choose two (the choices must be different)", benefits, new int[] {firstChoice});
        return new int[] {firstChoice, secondChoice};
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
		} else if (isRuins) {
			return "ruins";
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
			} else if (isRuins) {
				return "Action-Ruins";
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

	public enum MixedPileId {
		RUINS("Ruins"),
		KNIGHTS("Knights");

		private String str;

		MixedPileId(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return str;
		}

		public static MixedPileId fromString(String str) {
			if ("Ruins".equals(str)) {
				return RUINS;
			} else if ("Knights".equals(str)) {
				return KNIGHTS;
			} else {
				return null;
			}
		}
	}

	public boolean inMixedPile() {
		return mixedPileId() != null;
	}

	public MixedPileId mixedPileId() {
		if (isRuins) {
			return MixedPileId.RUINS;
		} else {
			return null;
		}
	}

}
