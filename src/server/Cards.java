package server;

import cards.*;

import java.util.*;

public class Cards {

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
    private static final Card BUREAUCRAT = new Bureaucrat();
    static final Card CELLAR = new Cellar();
    public static final Card CHAPEL = new Chapel();
    private static final Card COUNCIL_ROOM = new CouncilRoom();
    static final Card FESTIVAL = new Festival();
    private static final Card GARDENS = new Gardens();
    static final Card LABORATORY = new Laboratory();
    public static final Card LIBRARY = new Library();
    static final Card MARKET = new Market();
    private static final Card MILITIA = new Militia();
    static final Card MOAT = new Moat();
    private static final Card REMODEL = new Remodel();
    public static final Card SMITHY = new Smithy();
    static final Card VILLAGE = new Village();
    public static final Card WITCH = new Witch();
    private static final Card WORKSHOP = new Workshop();
    // base set (1st edition only)
    private static final Card ADVENTURER = new Adventurer();
    private static final Card CHANCELLOR = new Chancellor();
    private static final Card FEAST = new Feast();
    private static final Card MINE_FIRST_EDITION = new MineFirstEdition();
    private static final Card MONEYLENDER_FIRST_EDITION = new MoneylenderFirstEdition();
    static final Card SPY = new Spy();
    private static final Card THIEF = new Thief();
    private static final Card THRONE_ROOM_FIRST_EDITION = new ThroneRoomFirstEdition();
    private static final Card WOODCUTTER = new Woodcutter();
    // base set (2nd edition only)
    private static final Card ARTISAN = new Artisan();
    private static final Card BANDIT = new Bandit();
    private static final Card HARBINGER = new Harbinger();
    public static final Card MERCHANT = new Merchant();
    static final Card MINE = new Mine();
    static final Card MONEYLENDER = new Moneylender();
    private static final Card POACHER = new Poacher();
    private static final Card SENTRY = new Sentry();
    static final Card THRONE_ROOM = new ThroneRoom();
    private static final Card VASSAL = new Vassal();
    // intrigue expansion (cards in both 1st and 2nd editions)
    private static final Card BARON = new Baron();
    private static final Card BRIDGE = new Bridge();
    static final Card CONSPIRATOR = new Conspirator();
    private static final Card COURTYARD = new Courtyard();
    private static final Card DUKE = new Duke();
    private static final Card HAREM = new Harem();
    private static final Card IRONWORKS = new Ironworks();
    static final Card MINING_VILLAGE = new MiningVillage();
    static final Card MINION = new Minion();
    private static final Card NOBLES = new Nobles();
    private static final Card PAWN = new Pawn();
    private static final Card SHANTY_TOWN = new ShantyTown();
    private static final Card STEWARD = new Steward();
    private static final Card SWINDLER = new Swindler();
    private static final Card TORTURER = new Torturer();
    static final Card TRADING_POST = new TradingPost();
    private static final Card UPGRADE = new Upgrade();
    static final Card WISHING_WELL = new WishingWell();
    // intrigue expansion (1st edition only)
    static final Card COPPERSMITH = new Coppersmith();
    static final Card GREAT_HALL = new GreatHall();
    private static final Card MASQUERADE_FIRST_EDITION = new MasqueradeFirstEdition();
    private static final Card SABOTEUR = new Saboteur();
    private static final Card SCOUT = new Scout();
    private static final Card SECRET_CHAMBER = new SecretChamber();
    private static final Card TRIBUTE = new Tribute();
    // intrigue expansion (2nd edition only)
    private static final Card COURTIER = new Courtier();
    static final Card DIPLOMAT = new Diplomat();
    private static final Card LURKER = new Lurker();
    public static final Card MASQUERADE = new Masquerade();
    private static final Card MILL = new Mill();
    private static final Card PATROL = new Patrol();
    private static final Card REPLACE = new Replace();
    private static final Card SECRET_PASSAGE = new SecretPassage();
    // seaside expansion
    private static final Card EMBARGO = new Embargo();
    static final Card HAVEN = new Haven();
    static final Card LIGHTHOUSE = new Lighthouse();
    private static final Card NATIVE_VILLAGE = new NativeVillage();
    static final Card PEARL_DIVER = new PearlDiver();
    static final Card AMBASSADOR = new Ambassador();
    private static final Card FISHING_VILLAGE = new FishingVillage();
    private static final Card LOOKOUT = new Lookout();
    static final Card SMUGGLERS = new Smugglers();
    private static final Card WAREHOUSE = new Warehouse();
    static final Card CARAVAN = new Caravan();
    private static final Card CUTPURSE = new Cutpurse();
    private static final Card ISLAND = new Island();
    private static final Card NAVIGATOR = new Navigator();
    private static final Card PIRATE_SHIP = new PirateShip();
    private static final Card SALVAGER = new Salvager();
    private static final Card SEA_HAG = new SeaHag();
    static final Card TREASURE_MAP = new TreasureMap();
    static final Card BAZAAR = new Bazaar();
    private static final Card EXPLORER = new Explorer();
    private static final Card GHOST_SHIP = new GhostShip();
    private static final Card MERCHANT_SHIP = new MerchantShip();
    static final Card OUTPOST = new Outpost();
    static final Card TACTICIAN = new Tactician();
    static final Card TREASURY = new Treasury();
    public static final Card WHARF = new Wharf();
    // prosperity expansion
    private static final Card LOAN = new Loan();
    static final Card TRADE_ROUTE = new TradeRoute();
    static final Card WATCHTOWER = new Watchtower();
    private static final Card BISHOP = new Bishop();
    private static final Card MONUMENT = new Monument();
    static final Card QUARRY = new Quarry();
    static final Card TALISMAN = new Talisman();
    static final Card WORKERS_VILLAGE = new WorkersVillage();
    static final Card CITY = new City();
    static final Card CONTRABAND = new Contraband();
    static final Card COUNTING_HOUSE = new CountingHouse();
    static final Card MINT = new Mint();
    private static final Card MOUNTEBANK = new Mountebank();
    private static final Card RABBLE = new Rabble();
    static final Card ROYAL_SEAL = new RoyalSeal();
    private static final Card VAULT = new Vault();
    static final Card VENTURE = new Venture();
    static final Card GOONS = new Goons();
    static final Card GRAND_MARKET = new GrandMarket();
    static final Card HOARD = new Hoard();
    public static final Card BANK = new Bank();
    private static final Card EXPAND = new Expand();
    private static final Card FORGE = new Forge();
    static final Card KINGS_COURT = new KingsCourt();
    static final Card PEDDLER = new Peddler();
    // prosperity basic cards
    public static final Card PLATINUM = new Platinum();
    public static final Card COLONY = new Colony();
    // cornucopia expansion
    private static final Card HAMLET = new Hamlet();
    private static final Card FORTUNE_TELLER = new FortuneTeller();
    private static final Card MENAGERIE = new Menagerie();
    private static final Card FARMING_VILLAGE = new FarmingVillage();
    static final Card HORSE_TRADERS = new HorseTraders();
    private static final Card REMAKE = new Remake();
    static final Card TOURNAMENT = new Tournament();
    static final Card YOUNG_WITCH = new YoungWitch();
    private static final Card HARVEST = new Harvest();
    static final Card HORN_OF_PLENTY = new HornOfPlenty();
    private static final Card HUNTING_PARTY = new HuntingParty();
    private static final Card JESTER = new Jester();
    private static final Card FAIRGROUNDS = new Fairgrounds();
    // cornucopia prizes
    private static final Card BAG_OF_GOLD = new BagOfGold();
    static final Card DIADEM = new Diadem();
    private static final Card FOLLOWERS = new Followers();
    static final Card PRINCESS = new Princess();
    private static final Card TRUSTY_STEED = new TrustySteed();
    // hinterlands expansion
    private static final Card CROSSROADS = new Crossroads();
    static final Card DUCHESS = new Duchess();
    static final Card FOOLS_GOLD = new FoolsGold();
    private static final Card DEVELOP = new Develop();
    private static final Card OASIS = new Oasis();
    private static final Card ORACLE = new Oracle();
    static final Card SCHEME = new Scheme();
    static final Card TUNNEL = new Tunnel();
    public static final Card JACK_OF_ALL_TRADES = new JackOfAllTrades();
    static final Card NOBLE_BRIGAND = new NobleBrigand();
    static final Card NOMAD_CAMP = new NomadCamp();
    private static final Card SILK_ROAD = new SilkRoad();
    private static final Card SPICE_MERCHANT = new SpiceMerchant();
    static final Card TRADER = new Trader();
    private static final Card CACHE = new Cache();
    private static final Card CARTOGRAPHER = new Cartographer();
    private static final Card EMBASSY = new Embassy();
    static final Card HAGGLER = new Haggler();
    static final Card HIGHWAY = new Highway();
    static final Card ILL_GOTTEN_GAINS = new IllGottenGains();
    private static final Card INN = new Inn();
    private static final Card MANDARIN = new Mandarin();
    private static final Card MARGRAVE = new Margrave();
    private static final Card STABLES = new Stables();
    public static final Card BORDER_VILLAGE = new BorderVillage();
    static final Card FARMLAND = new Farmland();
    // dark ages expansion
    private static final Card POOR_HOUSE = new PoorHouse();
    static final Card BEGGAR = new Beggar();
    private static final Card SQUIRE = new Squire();
    private static final Card VAGRANT = new Vagrant();
    private static final Card FORAGER = new Forager();
    static final Card HERMIT = new Hermit();
    static final Card MARKET_SQUARE = new MarketSquare();
    private static final Card SAGE = new Sage();
    private static final Card STOREROOM = new Storeroom();
    static final Card URCHIN = new Urchin();
    private static final Card ARMORY = new Armory();
    private static final Card DEATH_CART = new DeathCart();
    private static final Card FEODUM = new Feodum();
    private static final Card FORTRESS = new Fortress();
    private static final Card IRONMONGER = new Ironmonger();
    static final Card MARAUDER = new Marauder();
    private static final Card PROCESSION = new Procession();
    private static final Card RATS = new Rats();
    private static final Card SCAVENGER = new Scavenger();
    private static final Card WANDERING_MINSTREL = new WanderingMinstrel();
    public static final Card BAND_OF_MISFITS = new BandOfMisfits();
    static final Card BANDIT_CAMP = new BanditCamp();
    private static final Card CATACOMBS = new Catacombs();
    private static final Card COUNT = new Count();
    static final Card COUNTERFEIT = new Counterfeit();
    private static final Card CULTIST = new Cultist();
    private static final Card GRAVEROBBER = new Graverobber();
    private static final Card JUNK_DEALER = new JunkDealer();
    static final Card KNIGHTS = new Knights();
    private static final Card MYSTIC = new Mystic();
    static final Card PILLAGE = new Pillage();
    private static final Card REBUILD = new Rebuild();
    private static final Card ROGUE = new Rogue();
    private static final Card ALTAR = new Altar();
    private static final Card HUNTING_GROUNDS = new HuntingGrounds();
    // dark ages ruins
    private static final Card ABANDONED_MINE = new AbandonedMine();
    private static final Card RUINED_LIBRARY = new RuinedLibrary();
    private static final Card RUINED_MARKET = new RuinedMarket();
    private static final Card RUINED_VILLAGE = new RuinedVillage();
    private static final Card SURVIVORS = new Survivors();
    // dark ages knights
    private static final Card DAME_ANNA = new DameAnna();
    private static final Card DAME_JOSEPHINE = new DameJosephine();
    private static final Card DAME_MOLLY = new DameMolly();
    private static final Card DAME_NATALIE = new DameNatalie();
    private static final Card DAME_SYLVIA = new DameSylvia();
    private static final Card SIR_BAILEY = new SirBailey();
    private static final Card SIR_DESTRY = new SirDestry();
    private static final Card SIR_MARTIN = new SirMartin();
    private static final Card SIR_MICHAEL = new SirMichael();
    private static final Card SIR_VANDER = new SirVander();
    // dark ages non-supply
    static final Card MADMAN = new Madman();
    static final Card MERCENARY = new Mercenary();
    public static final Card SPOILS = new Spoils();
    // dark ages shelters
    static final Card HOVEL = new Hovel();
    static final Card NECROPOLIS = new Necropolis();
    static final Card OVERGROWN_ESTATE = new OvergrownEstate();
    // guilds expansion
    private static final Card CANDLESTICK_MAKER = new CandlestickMaker();
    private static final Card STONEMASON = new Stonemason();
    private static final Card DOCTOR = new Doctor();
    private static final Card MASTERPIECE = new Masterpiece();
    public static final Card ADVISOR = new Advisor();
    private static final Card PLAZA = new Plaza();
    private static final Card TAXMAN = new Taxman();
    private static final Card HERALD = new Herald();
    static final Card BAKER = new Baker();
    private static final Card BUTCHER = new Butcher();
    private static final Card JOURNEYMAN = new Journeyman();
    static final Card MERCHANT_GUILD = new MerchantGuild();
    private static final Card SOOTHSAYER = new Soothsayer();

    static Map<String, Card> cardsByName = new HashMap<>();

    static Set<Card> BASIC_CARDS = new HashSet<>();
    static Set<Card> BASE_SET = new HashSet<>();
    private static Set<Card> BASE_SET_FIRST_EDITION = new HashSet<>();
    private static Set<Card> INTRIGUE_SET = new HashSet<>();
    private static Set<Card> INTRIGUE_SET_FIRST_EDITION = new HashSet<>();
    private static Set<Card> SEASIDE_SET = new HashSet<>();
    static Set<Card> PROSPERITY_SET = new HashSet<>();
    static Set<Card> PROSPERITY_BASIC_CARDS = new HashSet<>();
    private static Set<Card> CORNUCOPIA_SET = new HashSet<>();
    static Set<Card> PRIZE_CARDS = new HashSet<>();
    private static Set<Card> HINTERLANDS_SET = new HashSet<>();
    static Set<Card> DARK_AGES_SET = new HashSet<>();
    static Set<Card> RUINS_CARDS = new HashSet<>();
    static Set<Card> KNIGHT_CARDS = new HashSet<>();
    private static Set<Card> DARK_AGES_NON_SUPPLY_CARDS = new HashSet<>();
    static Set<Card> SHELTER_CARDS = new HashSet<>();
    private static Set<Card> GUILDS_SET = new HashSet<>();

    static Map<String, Set<Card>> setsByName = new HashMap<>();
    static List<Set<Card>> setOrder = new ArrayList<>();
    static List<String> setNames = new ArrayList<>();

    static void initializeCardSets() {

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
        include(BUTCHER, GUILDS_SET);
        include(JOURNEYMAN, GUILDS_SET);
        include(MERCHANT_GUILD, GUILDS_SET);
        include(SOOTHSAYER, GUILDS_SET);
    }

    private static void include(Set<Card> cardSet, String name) {
        setOrder.add(cardSet);
        setNames.add(name);
        setsByName.put(name, cardSet);
    }

    private static void include(Card card, Set<Card> kingdomSet) {
        cardsByName.put(card.toString(), card);
        // include simplified name user-typed cards to ignore capitalization and punctuation
        cardsByName.put(simplifiedName(card.toString()), card);
        if (kingdomSet != null) {
            kingdomSet.add(card);
        }
    }

    static String simplifiedName(String name) {
        return name.toLowerCase().replaceAll("[^a-z]", "");
    }

    static Card fromName(String name) {
        return cardsByName.get(name);
    }

    private Cards() {}

}
