package server;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import bots.Mimic;
import cards.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Game implements Runnable {

    private static final Comparator<Card> KINGDOM_ORDER_COMPARATOR =
            (c1, c2) -> {
                int c1_cost = c1.cost();
                int c2_cost = c2.cost();
                if (c1_cost != c2_cost) {
                    // order by highest cost
                    return c2_cost - c1_cost;
                } else {
                    // order alphabetically
                    return c1.toString().compareTo(c2.toString());
                }
            };

    private static final Comparator<Card> BASIC_ORDER_COMPARATOR =
            (c1, c2) -> {
                int c1_type = BASIC_ORDER_TYPE(c1);
                int c2_type = BASIC_ORDER_TYPE(c2);
                if (c1_type != c2_type) {
                    // victories < coins < curses
                    return c1_type - c2_type;
                } else {
                    // order by cost
                    return c2.cost() - c1.cost();
                }
            };

    private static int BASIC_ORDER_TYPE(Card card) {
        if (card.isVictory()) {
            return 1;
        } else if (card.isTreasure()) {
            return 2;
        } else {
            return 3;
        }
    }

    private static final Comparator<Card> TREASURE_PLAY_ORDER_COMPARATOR =
            (c1, c2) -> {
                int c1Type = TREASURE_PLAY_ORDER_TYPE(c1);
                int c2Type = TREASURE_PLAY_ORDER_TYPE(c2);
                if (c1Type != c2Type) {
                    // contraband < other treasures cards < horn of plenty < bank
                    return c1Type - c2Type;
                } else {
                    // order alphabetically
                    return c1.toString().compareTo(c2.toString());
                }
            };

    private static int TREASURE_PLAY_ORDER_TYPE(Card card) {
        if (card == Cards.COUNTERFEIT) {
            // play Couterfeit first because it gives you the opportunity to play other treasures twice
            return 0;
        }
        if (card == Cards.CONTRABAND) {
            // play contraband early so opponents don't know exactly how much coin you have when they prohibit you
            // from buying something
            return 1;
        } else if (card == Cards.HORN_OF_PLENTY) {
            // play horn of plenty late to maximize its gaining potential
            return 3;
        } else if (card == Cards.BANK) {
            // play bank last to maximize its value
            return 4;
        } else {
            return 2;
        }
    }

    private GameServer server;

    public List<Player> players;
    private int currentPlayerIndex;

    private Set<Card> kingdomCards;
    private Set<Card> basicCards;
    public Card baneCard;
    public Set<Card> prizeCards;
    private boolean usingShelters;
    public Map<Card, Integer> supply = new HashMap<>();
    public Map<Card.MixedPileId, List<Card>> mixedPiles = new HashMap<>();
    public Map<Card, Integer> nonSupply = new HashMap<>();
    List<Card> trash = new ArrayList<>();

    boolean isGameOver;

    // various bits of game state required by individual card rules
    public boolean playedSilverThisTurn;
    public int cardCostReduction;
    public int actionsPlayedThisTurn;
    public int coppersmithsPlayedThisTurn;
    Map<Card, Integer> embargoTokens = new HashMap<>();
    Map<Card.MixedPileId, Integer> mixedPileEmbargoTokens = new HashMap<>();
    private boolean boughtCardThisTurn;
    private boolean boughtVictoryCardThisTurn;
    public Set<Card> contrabandProhibited = new HashSet<>();
    Set<Card> tradeRouteTokenedPiles = new HashSet<>();
    public int tradeRouteMat;
    public boolean inBuyPhase;
    public boolean playedCrossroadsThisTurn;
    public int schemesPlayedThisTurn;

    // ui state
    public int messageIndent;

    // setup conditions
    private Set<Set<Card>> cardSets;
    private Set<Card> requiredCards;
    private Set<Card> forbiddenCards;

    public Game(GameServer server, Set<Player> playerSet, Set<Set<Card>> cardSets, Set<Card> requiredCards, Set<Card> forbiddenCards) {
        this.server = server;
        playerSet.forEach(player -> player.game = this);
        players = new ArrayList<>(playerSet);
        this.cardSets = cardSets;
        this.requiredCards = requiredCards;
        this.forbiddenCards = forbiddenCards;
    }

    @Override
    public void run() {
        setup();
        while (!gameOverConditionMet()) {
            takeTurn(currentPlayer());
            if (!currentPlayer().hasExtraTurn()) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }
        announceWinner();
        endGame();
    }

    private void setup() {
        chooseCardsInGame();
        setupSupply();
        additionalSetup();
        // randomize turn order
        Collections.shuffle(players);
        // send each player the initial game state
        for (Player player : players) {
            enterGame(player);
            player.newGameSetup(usingShelters);
        }
    }

    private void additionalSetup() {
        // give each player a coin token if Baker is in play
        if (kingdomCards.contains(Cards.BAKER)) {
            players.forEach(p -> p.addCoinTokens(1));
        }
        // start recording gain strategies
        initRecords();
    }

    @SuppressWarnings("unchecked")
    private void enterGame(Player player) {
        // send players to the game screen
        JSONObject command = new JSONObject();
        command.put("command", "enterGame");

        command.put("cardInfo", jsonCardInfo());
        command.put("basicPiles", jsonBasicPiles());
        command.put("kingdomPiles", jsonKingdomPiles());
        command.put("nonSupplyPiles", jsonNonSupplyPiles());
        if (!prizeCards.isEmpty()) {
            command.put("prizeCards", jsonPrizeCards());
        }
        command.put("opponents", players.size() - 1);

        player.sendCommand(command);
    }

    @SuppressWarnings("unchecked")
    private JSONObject jsonCardInfo() {
        JSONObject cardInfo = new JSONObject();
        allCardsInGame().forEach(c -> cardInfo.put(c.toString(), jsonCardInfo(c)));
        return cardInfo;
    }

    @SuppressWarnings("unchecked")
    private JSONObject jsonCardInfo(Card card) {
        JSONObject cardInfo = new JSONObject();
        cardInfo.put("highlightType", card.htmlHighlightType());
        cardInfo.put("type", card.htmlType());
        cardInfo.put("cost", "$" + card.cost());
        JSONArray description = new JSONArray();
        description.addAll(Arrays.asList(card.description()));
        cardInfo.put("description", description);
        return cardInfo;
    }

    @SuppressWarnings("unchecked")
    private JSONArray jsonBasicPiles() {
        List<Card> basicCardsSorted = new ArrayList<>(basicCards);
        basicCardsSorted.sort(BASIC_ORDER_COMPARATOR);
        JSONArray piles = new JSONArray();
        basicCardsSorted.forEach(c -> piles.add(jsonPile(c)));
        return piles;
    }

    @SuppressWarnings("unchecked")
    private JSONArray jsonKingdomPiles() {
        List<Card> kingdomTopCardsSorted = new ArrayList<>(kingdomCards);
        // add the top cards from mixed piles
        mixedPiles.values().forEach(mixedCards -> kingdomTopCardsSorted.add(mixedCards.get(0)));
        kingdomTopCardsSorted.sort(KINGDOM_ORDER_COMPARATOR);
        JSONArray piles = new JSONArray();
        kingdomTopCardsSorted.forEach(c -> piles.add(jsonPile(c)));
        return piles;
    }

    @SuppressWarnings("unchecked")
    private JSONArray jsonNonSupplyPiles() {
        List<Card> cards = new ArrayList<>(nonSupply.keySet());
        // just order non-supply piles by name
        cards.sort(Comparator.comparing(Object::toString));
        JSONArray piles = new JSONArray();
        cards.forEach(c -> piles.add(jsonPile(c)));
        return piles;
    }

    private JSONObject jsonPile(Card card) {
        JSONObject pile = new JSONObject();
        if (card.inMixedPile()) {
            String mixedPileId = card.mixedPileId().toString();
            pile.put("id", mixedPileId);
            pile.put("topCard", card.toString());
            pile.put("subtitle", "(" + mixedPileId + ")");
        } else {
            pile.put("id", card.toString());
            if (card == baneCard) {
                pile.put("subtitle", "(Bane)");
            }
        }
        return pile;
    }

    private JSONArray jsonPrizeCards() {
        List<String> prizeCardNames = prizeCards.stream()
                .map(Card::toString)
                .collect(Collectors.toList());
        // just order prize cards by name
        Collections.sort(prizeCardNames);
        JSONArray jsonPrizeCards = new JSONArray();
        prizeCardNames.forEach(jsonPrizeCards::add);
        return jsonPrizeCards;
    }


    private void chooseCardsInGame() {
        // if there are any Mimic bots
        if (players.stream().anyMatch(p -> p instanceof Mimic)) {
            // if there is an available strategy
            if (server.hasAnyWinningStrategy()) {
                // pick a random card set with a strategy
                requiredCards = server.randomWinningStrategyCardSet();
                List<Card> strategy = server.winningStrategyForCardSet(requiredCards);
                // set all bots to use the strategy for that set
                players.forEach(player -> {
                    if (player instanceof Mimic) {
                        Mimic mimicBot = (Mimic) player;
                        mimicBot.setStrategy(strategy);
                    }
                });
            } else {
                // if there is no available strategy, replace them all with BigMoney bots
                int numToReplace = (int) players.stream()
                        .filter(p -> p instanceof Mimic)
                        .count();
                players = players.stream()
                        .filter(p -> !(p instanceof Mimic))
                        .collect(Collectors.toList());
                for (int n = 0; n < numToReplace; n++) {
                    Bot bot = new Bot();
                    bot.game = this;
                    players.add(bot);
                }
            }
        }
        // add the required cards for each bot, replacing any bot that can't have its required cards with a BigMoney bot
        int numToReplace = 0;
        for (Iterator<Player> iter = players.iterator(); iter.hasNext(); ) {
            Player next = iter.next();
            if (next instanceof Bot) {
                Set<Card> requiredForBot = new HashSet<>(requiredCards);
                requiredForBot.addAll(((Bot) next).required());
                if (requiredForBot.size() > 10) {
                    iter.remove();
                    numToReplace++;
                } else {
                    requiredCards = requiredForBot;
                }
            }
        }
        for (int n = 0; n < numToReplace; n++) {
            Bot bot = new Bot();
            bot.game = this;
            players.add(bot);
        }
        // add all of the required cards to the kingdom
        kingdomCards = new HashSet<>(requiredCards);
        // if there are too many required cards, take the first 10
        if (kingdomCards.size() > 10) {
            kingdomCards = new HashSet<>((new ArrayList<>(kingdomCards)).subList(0, 10));
        }
        // create a list of available kingdom cards to fill in the rest
        Set<Card> available = new HashSet<>();
        cardSets.forEach(available::addAll);
        // take out the forbidden cards
        available.removeAll(forbiddenCards);
        // shuffle and draw the remaining
        List<Card> availableList = new ArrayList<>(available);
        Collections.shuffle(availableList);
        int toDraw = Math.max(10 - kingdomCards.size(), 0);
        kingdomCards.addAll(availableList.subList(0, Math.min(toDraw, availableList.size())));
        // if there are still not 10, fill in the rest with the basic set (completely overriding user requests)
        if (kingdomCards.size() < 10) {
            Set<Card> filler = new HashSet<>(Cards.BASE_SET);
            filler.removeAll(kingdomCards);
            List<Card> fillerList = new ArrayList<>(filler);
            Collections.shuffle(fillerList);
            kingdomCards.addAll(fillerList.subList(0, 10 - kingdomCards.size()));
        }
        // if Young Witch is in the supply, choose a bane card
        if (kingdomCards.contains(Cards.YOUNG_WITCH)) {
            // if there is no acceptable bane card in the requested sets, choose one from another set
            Set<Card> backupBaneOptions = new HashSet<>();
            Cards.setsByName.values().forEach(backupBaneOptions::addAll);
            for (Set<Card> baneSet : Arrays.asList(available, backupBaneOptions)) {
                Set<Card> baneChoices = new HashSet<>(baneSet);
                // the bane card must be a new 11th card (not one of the 10 kingdom cards already chosen)
                baneChoices.removeAll(kingdomCards);
                // make sure the bane card costs 2 or 3
                List<Card> baneChoicesList = baneChoices.stream()
                        .filter(c -> c.cost() == 2 || c.cost() == 3)
                        .collect(Collectors.toList());
                // if there are any acceptable bane cards
                if (!baneChoicesList.isEmpty()) {
                    // choose one at random
                    baneCard = baneChoicesList.get((int) (Math.random() * baneChoicesList.size()));
                    break;
                }
            }
            // if Young Witch is in the kingdom, a bane card must be chosen
            if (baneCard == null) {
                throw new IllegalStateException();
            }
            // add the bane card to the kingdom cards
            kingdomCards.add(baneCard);
        }
        // if tournament is in the supply, add prize cards
        prizeCards = new HashSet<>();
        if (kingdomCards.contains(Cards.TOURNAMENT)) {
            prizeCards.addAll(Cards.PRIZE_CARDS);
        }
        // basic cards
        basicCards = new HashSet<>(Cards.BASIC_CARDS);
        // somewhat-randomly choose whether to include platinum and colony
        if (proportionDeterminedSufficient(kingdomCards, Cards.PROSPERITY_SET)) {
            basicCards.addAll(Cards.PROSPERITY_BASIC_CARDS);
        }
        // somewhat-randomly choose whether to play with shelters
        usingShelters = proportionDeterminedSufficient(kingdomCards, Cards.DARK_AGES_SET);
    }

    /**
     * Returns true if the there are enough of the given card set's cards in the kingdom to use that set's custom rules.
     * This is always true if all cards in the kingdom are from that set, always false if none are, and random with
     * probability equal to the proportion of cards from that set otherwise.
     */
    private boolean proportionDeterminedSufficient(Set<Card> kingdomCards, Set<Card> cardSet) {
        int numFromSet = (int) kingdomCards.stream().filter(cardSet::contains).count();
        return (int) (Math.random() * kingdomCards.size()) < numFromSet;
    }

    private void setupSupply() {
        // remove Knights placeholder card and set up mixed Knight pile
        if (kingdomCards.contains(Cards.KNIGHTS)) {
            kingdomCards.remove(Cards.KNIGHTS);
            setupKnightPile();
        }
        // initialize kingdom piles
        for (Card card : kingdomCards) {
            supply.put(card, card.startingSupply(players.size()));
        }
        // initialize non-supply piles
        setupNonSupply();
        // initialize ruins pile
        if (kingdomCards.stream().anyMatch(Card::isLooter)) {
            setupRuinsPile();
        }
        // initialize basic piles
        for (Card card : basicCards) {
            supply.put(card, card.startingSupply(players.size()));
        }
        // initialize embargo tokens
        for (Card cardInSupply : supply.keySet()) {
            embargoTokens.put(cardInSupply, 0);
        }
        for (Card.MixedPileId pileId : mixedPiles.keySet()) {
            mixedPileEmbargoTokens.put(pileId, 0);
        }
        // initialize trade route tokens
        if (supply.containsKey(Cards.TRADE_ROUTE)) {
            tradeRouteTokenedPiles = supply.keySet().stream()
                    .filter(Card::isVictory)
                    .collect(Collectors.toSet());
        }
    }

    private void setupKnightPile() {
        // add one of each Knight
        List<Card> knightPile = new ArrayList<>(Cards.KNIGHT_CARDS);
        // shuffle
        Collections.shuffle(knightPile);
        mixedPiles.put(Card.MixedPileId.KNIGHTS, knightPile);
    }

    private void setupRuinsPile() {
        List<Card> ruinsPile = new ArrayList<>();
        // add 10 of each ruins card
        for (Card ruinsCard : Cards.RUINS_CARDS) {
            for (int i = 0; i < 10; i++) {
                ruinsPile.add(ruinsCard);
            }
        }
        // shuffle and take 10 for a 2 players, 20 for 3 player, etc.
        Collections.shuffle(ruinsPile);
        ruinsPile = new ArrayList<>(ruinsPile.subList(0, 10 * (players.size() - 1)));
        mixedPiles.put(Card.MixedPileId.RUINS, ruinsPile);
    }

    private void setupNonSupply() {
        if (kingdomCards.contains(Cards.HERMIT)) {
            nonSupply.put(Cards.MADMAN, 10);
        }
        if (kingdomCards.contains(Cards.URCHIN)) {
            nonSupply.put(Cards.MERCENARY, 10);
        }
        // Spoils are necessary for Marauder, Bandit Camp, and Pillage
        if (!Collections.disjoint(kingdomCards, Arrays.asList(Cards.MARAUDER, Cards.BANDIT_CAMP, Cards.PILLAGE))) {
            nonSupply.put(Cards.SPOILS, 15);
        }
    }

    private void takeTurn(Player player) {
        player.startNewTurn();
        cardCostReduction = 0;
        playedSilverThisTurn = false;
        actionsPlayedThisTurn = 0;
        boughtCardThisTurn = false;
        boughtVictoryCardThisTurn = false;
        contrabandProhibited.clear();
        playedCrossroadsThisTurn = false;
        schemesPlayedThisTurn = 0;
        newTurnMessage(player);
        messageIndent++;
        // resolve durations
        resolveDurations(player);
        while (player.actions > 0 && player.hasPlayableAction()) {
            // action phase
            Set<Card> choices = player.getHand().stream()
                    .filter(Card::isAction)
                    .collect(Collectors.toSet());
            Card choice = promptChoosePlay(
                    player,
                    choices,
                    "Action Phase: Play actions from your hand.",
                    "No Action"
            );
            if (choice == null) {
                break;
            }
            // put action card in play
            player.putFromHandIntoPlay(choice);
            player.actions -= 1;
            // play action
            playAction(player, choice, false);
        }
        boolean givenBuyPrompt = false;
        enterBuyPhase();
        while (player.buys > 0 && (hasUnplayedTreasure(player) || !buyableCards(player).isEmpty())) {
            // buy phase
            givenBuyPrompt = true;
            BuyPhaseChoice choice = promptBuyPhase(player);
            if (choice.toBuy != null) {
                // choose how much to overpay
                int amountOverpaid = chooseOverpay(player, choice.toBuy);
                // autoplay treasures
                if (choice.toBuy.cost(this) + amountOverpaid > player.coins) {
                    playAllTreasures(player);
                    // if the coin prediction was wrong and we let the user choose something that they couldn't actually buy
                    if (!buyableCards(player).contains(choice.toBuy)) {
                        throw new IllegalStateException();
                    }
                }
                message(player, "You buy " + choice.toBuy.htmlName());
                messageOpponents(player, player.username + " buys " + choice.toBuy.htmlName());
                // deduct 1 buy and spent coins
                player.buys -= 1;
                player.coins -= (choice.toBuy.cost(this) + amountOverpaid);
                // overpaying effect
                if (amountOverpaid != 0) {
                    messageIndent++;
                    messageAll("overpaying $" + amountOverpaid);
                    messageIndent++;
                    choice.toBuy.onOverpay(player, this, amountOverpaid);
                    messageIndent--;
                    messageIndent--;
                }
                // if the card can be gained (it's supply may have been depleted by an overpaying effect)
                if (isAvailableInSupply(choice.toBuy)) {
                    // gain purchased card
                    gain(player, choice.toBuy);
                    onBuy(player, choice.toBuy);
                    // record purchases for MimicBot
                    recordPlayerGained(player, choice.toBuy);
                }
            } else if (choice.toPlay != null) {
                message(player, "You play " + choice.toPlay.htmlName());
                messageOpponents(player, player.username + " plays " + choice.toPlay.htmlName());
                playTreasure(player, choice.toPlay);
            } else if (choice.isPlayingAllTreasures) {
                playAllTreasures(player);
            } else if (choice.coinTokensToSpend != 0) {
                String coinTokensStr = choice.coinTokensToSpend + " coin token";
                if (choice.coinTokensToSpend != 1) {
                    coinTokensStr += "s";
                }
                message(player, "You spend " + coinTokensStr);
                messageOpponents(player, player.username + " spends " + coinTokensStr);
                player.addCoinTokens(-choice.coinTokensToSpend);
                player.coins += choice.coinTokensToSpend;
            } else if (choice.isEndingTurn) {
                break;
            }
        }
        coppersmithsPlayedThisTurn = 0;
        exitBuyPhase();
        // if the player couldn't buy anything, notify them that their turn is over
        // (without this prompt, the game could get caught in a non-interactive infinite loop if all players can do nothing on their turns)
        if (!givenBuyPrompt) {
            if (!(player instanceof Bot)) {
                new Prompt(player, this)
                        .message("There are no cards that you can buy this turn.")
                        .multipleChoices(new String[]{"End Turn"})
                        .response();
            }
        }
        cleanup(player);
        messageIndent--;
    }

    private void resolveDurations(Player player) {
        List<DurationEffect> effects = new ArrayList<>(player.getDurationEffects());
        for (DurationEffect effect : effects) {
            messageAll(effect.card.htmlNameRaw() + " takes effect");
            messageIndent++;
            effect.card.onDurationEffect(player, this, effect);
            messageIndent--;
        }
        player.cleanupDurations();
    }

    public void playTreasure(Player player, Card treasure) {
        messageIndent++;
        player.putFromHandIntoPlay(treasure);
        player.coins += treasure.treasureValue(this);
        treasure.onPlay(player, this, false);
        messageIndent--;
    }

    private void playAllTreasures(Player player) {
        List<Card> treasures = player.getHand().stream()
                .filter(Card::isTreasure)
                .collect(Collectors.toList());
        if (!treasures.isEmpty()) {
            message(player, "You play " + Card.htmlList(treasures));
            messageOpponents(player, player.username + " plays " + Card.htmlList(treasures));
            Collections.sort(treasures, TREASURE_PLAY_ORDER_COMPARATOR);
            messageIndent++;
            for (Card treasure : treasures) {
                playTreasure(player, treasure);
            }
            messageIndent--;
        }
    }

    private int chooseOverpay(Player player, Card toBuy) {
        if (toBuy.isOverpayable() && player.getUsableCoins() > toBuy.cost(this)) {
            int maxOverpayable = player.getUsableCoins() - toBuy.cost(this);
            if (player instanceof Bot) {
                int toOverpay = ((Bot) player).chooseOverpay(maxOverpayable, toBuy);
                Card.checkMultipleChoice(maxOverpayable, toOverpay+1);
                return toOverpay;
            }
            String[] choices = new String[maxOverpayable + 1];
            choices[0] = "Don't overpay";
            for (int i = 0; i < maxOverpayable; i++) {
                choices[i + 1] = "" + (i + 1);
            }
            return new Prompt(player, this)
                    .type(Prompt.Type.BUY)
                    .message(toBuy.toString() + ": Overpay by how much?")
                    .multipleChoices(choices)
                    .responseMultipleChoiceIndex();
        } else {
            return 0;
        }
    }

    // TODO: rewrite
    public int numTreasuresInPlay() {
        int num = 0;
        for (Card card : currentPlayer().getPlay()) {
            if (card.isTreasure()) {
                num++;
            }
        }
        return num;
    }

    // TODO: rewrite
    public int numActionsInPlay() {
        int num = 0;
        for (Card card : currentPlayer().getPlay()) {
            if (card.isAction()) {
                num++;
            }
        }
        return num;
    }

    private void onBuy(Player player, Card card) {
        messageIndent++;
        boughtCardThisTurn = true;
        if (card.isVictory()) {
            boughtVictoryCardThisTurn = true;
        }
        // if that card's pile was embargoed
        if (embargoTokensOn(card) != 0 && supply.get(Cards.CURSE) != 0) {
            int numEmbargoTokens = embargoTokensOn(card);
            int cursesToGain = Math.min(numEmbargoTokens, supply.get(Cards.CURSE));
            messageAll("gaining " + Cards.CURSE.htmlName(cursesToGain));
            for (int i = 0; i < numEmbargoTokens && supply.get(Cards.CURSE) != 0; i++) {
                gain(player, Cards.CURSE);
            }
        }
        // if the purchase can be affected by talisman
        if (card.cost(this) <= 4 && !card.isVictory() && isAvailableInSupply(card)) {
            int numTalismans = numberInPlay(Cards.TALISMAN);
            // if the player has talismans in play
            if (numTalismans != 0) {
                if (supply.containsKey(card)) {
                    int copiesToGain = Math.min(numTalismans, supply.get(card));
                    if (copiesToGain == 1) {
                        messageAll("gaining another " + card.htmlNameRaw() + " because of " + Cards.TALISMAN.htmlNameRaw());
                    } else {
                        messageAll("gaining another " + card.htmlName(copiesToGain) + " because of " + Cards.TALISMAN.htmlNameRaw());
                    }
                    for (int i = 0; i < copiesToGain; i++) {
                        gain(player, card);
                    }
                } else {
                    for (int i = 0; i < numTalismans && isAvailableInSupply(card); i++) {
                        messageAll("gaining another " + card.htmlNameRaw() + " because of " + Cards.TALISMAN.htmlNameRaw());
                        gain(player, card);
                    }
                }
            }
        }
        // if the purchase can be affected by hoard
        if (card.isVictory() && supply.get(Cards.GOLD) != 0) {
            int numHoards = (int) currentPlayer().getPlay().stream()
                    .filter(c -> c instanceof Hoard)
                    .count();
            // if the player has hoards in play
            if (numHoards != 0) {
                int goldsToGain = Math.min(numHoards, supply.get(Cards.GOLD));
                messageAll("gaining " + Cards.GOLD.htmlName(goldsToGain) + " because of " + Cards.HOARD.htmlNameRaw());
                for (int i = 0; i < numHoards && supply.get(Cards.GOLD) != 0; i++) {
                    gain(player, Cards.GOLD);
                }
            }
        }
        // if the purchase was a Mint, trash all treasures in play
        if (card == Cards.MINT) {
            List<Card> treasures = player.removeAllTreasuresFromPlay();
            if (!treasures.isEmpty()) {
                messageAll("trashing " + Card.htmlList(treasures) + " from play");
                trash(player, treasures);
            }
        }
        // if the purchase can be affected by Goons
        int numGoons = (int) currentPlayer().getPlay().stream()
                .filter(c -> c instanceof Goons)
                .count();
        if (numGoons != 0) {
            messageAll("gaining +" + numGoons + " VP because of " + Cards.GOONS.htmlNameRaw());
            player.addVictoryTokens(numGoons);
        }
        // if the purchase can be affected by Hagglers
        int numHagglers = (int) currentPlayer().getPlay().stream()
                .filter(c -> c instanceof Haggler)
                .count();
        for (int i = 0; i < numHagglers; i++) {
            // gain card costing less than the purchased card that is not a victory card
            Set<Card> gainable = cardsCostingAtMost(card.cost(this) - 1);
            gainable = gainable.stream()
                    .filter(c -> !c.isVictory())
                    .collect(Collectors.toSet());
            if (!gainable.isEmpty()) {
                Card toGain = ((Haggler) Cards.HAGGLER).chooseGain(
                        player,
                        this,
                        gainable
                );
                messageAll("gaining " + toGain.htmlName() + " because of " + Cards.HAGGLER.htmlNameRaw());
                gain(player, toGain);
            } else {
                break;
            }
        }
        // if the purchase can be affected by Merchant Guild
        int numMerchantGuilds = (int) currentPlayer().getPlay().stream()
                .filter(c -> c instanceof MerchantGuild)
                .count();
        if (numMerchantGuilds != 0) {
            // gain a coin token for each Merchant Guild
            messageAll("gaining " + numMerchantGuilds + " coin token" + (numMerchantGuilds == 1 ? "" : "s") + " because of " + Cards.MERCHANT_GUILD.htmlNameRaw());
            currentPlayer().addCoinTokens(numMerchantGuilds);
        }
        // if the card is Noble Brigand, do on-buy effect
        if (card == Cards.NOBLE_BRIGAND) {
            ((NobleBrigand) Cards.NOBLE_BRIGAND).onBuyOrPlay(player, this, getOpponents(player));
        }
        // on buying Farmland, trash a card and gain one costing exactly $2 more
        if (card == Cards.FARMLAND) {
            ((Farmland) Cards.FARMLAND).onBuy(player, this);
        }
        // when you buy a victory card, you may trash Hovel
        while (card.isVictory() && player.getHand().contains(Cards.HOVEL)) {
            if (((Hovel) Cards.HOVEL).chooseTrash(player, this)) {
                message(player, "trashing " + Cards.HOVEL.htmlName() + " from your hand");
                messageOpponents(player, "trashing " + Cards.HOVEL.htmlName() + " from their hand");
                player.removeFromHand(Cards.HOVEL);
                trash(player, Cards.HOVEL);
            } else {
                break;
            }
        }
        messageIndent--;
    }

    private int embargoTokensOn(Card card) {
        if (card.inMixedPile()) {
            return mixedPileEmbargoTokens.get(card.mixedPileId());
        } else {
            return embargoTokens.get(card);
        }
    }

    public boolean isAvailableInSupply(Card card) {
        if (card.inMixedPile()) {
            List<Card> mixedPile = mixedPiles.get(card.mixedPileId());
            return !mixedPile.isEmpty() && mixedPile.get(0) == card;
        }
        return supply.containsKey(card) && supply.get(card) != 0;
    }

    public boolean playAction(Player player, Card action, boolean hasMoved) {
        boolean moves = false;
        actionsPlayedThisTurn++;
        if (!action.isBandOfMisfits) {
            message(player, "You play " + action.htmlName());
            messageOpponents(player, player.username + " plays " + action.htmlName());
        } else {
            messageAll("playing it as " + action.htmlName());
        }
        messageIndent++;
        if (!action.isDuration()) {
            if (action.isAttack()) {
                // handle Urchins before the attack resolves
                handleUrchins(player, action);
                // attack reactions
                List<Player> targets = new ArrayList<>();
                for (Player opponent : getOpponents(player)) {
                    messageIndent++;
                    boolean unaffected = reactToAttack(opponent);
                    messageIndent--;
                    if (!unaffected) {
                        targets.add(opponent);
                    }
                }
                moves = action.onAttack(player, this, targets, hasMoved);
            } else {
                moves = action.onPlay(player, this, hasMoved);
            }
        } else {
            List<Card> toHaven = null;
            if (action == Cards.HAVEN) {
                toHaven = new ArrayList<>();
            }
            boolean willHaveEffect = action.onDurationPlay(player, this, toHaven);
            if (willHaveEffect) {
                // take this action out of normal play and save it as a duration effect
                player.removeFromPlay(action);
                player.addDurationEffect(action, toHaven);
                if (!hasMoved) {
                    player.addDurationSetAside(action);
                    moves = true;
                }
            }
        }
        messageIndent--;
        return moves;
    }

    private void handleUrchins(Player player, Card trigger) {
        List<Urchin> urchins = player.getPlay().stream()
                .filter(Urchin.class::isInstance)
                .map(Urchin.class::cast)
                .collect(Collectors.toList());
        // Urchin cannot trigger itself, even if played multiple times (like via Throne Room)
        //noinspection SuspiciousMethodCalls
        urchins.remove(trigger);
        for (Urchin urchin : urchins) {
            if (chooseTrashUrchinForMercenary(player)) {
                messageAll("trashing " + urchin.htmlName() + " from play" + (nonSupply.get(Cards.MERCENARY) != 0 ? (" and gaining " + Cards.MERCENARY.htmlName()) : ""));
                player.removeFromPlay(urchin);
                trash(player, urchin);
                if (nonSupply.get(Cards.MERCENARY) != 0) {
                    gain(player, Cards.MERCENARY);
                }
            } else {
                break;
            }
        }
    }

    private boolean chooseTrashUrchinForMercenary(Player player) {
        if (player instanceof Bot) {
            return ((Bot) player).urchinTrashForMercenary();
        }
        int choice = new Prompt(player, this)
                .message(Cards.URCHIN.toString() + ": Trash " + Cards.URCHIN.htmlName() + (nonSupply.get(Cards.MERCENARY) != 0 ? (" and gain " + Cards.MERCENARY.htmlName()) : "") + "?")
                .multipleChoices(new String[]{"Yes", "No"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private boolean reactToAttack(Player player) {
        if (player.getDurationSetAsideCards().stream()
                .anyMatch(effect -> effect instanceof Lighthouse)) {
            message(player, "You have " + Cards.LIGHTHOUSE.htmlName() + " in play");
            messageOpponents(player, player.username + " has " + Cards.LIGHTHOUSE.htmlName() + " in play");
            return true;
        }
        boolean unaffected = false;
        Set<Card> reactions = getAttackReactions(player);
        if (reactions.size() > 0) {
            do {
                Card choice = chooseAttackReaction(player, reactions);
                if (choice != null) {
                    message(player, "You reveal " + choice.htmlName());
                    messageOpponents(player, player.username + " reveals " + choice.htmlName());
                    messageIndent++;
                    unaffected |= choice.onAttackReaction(player, this);
                    messageIndent--;
                    // update possible reactions
                    reactions = getAttackReactions(player);
                    // don't allow the same reaction to be played twice in a row
                    // (they are designed so that playing them twice in a row gives no new benefit, with the exception of Diplomat)
                    // (also exempt reaction cards that move themselves, like Horse Traders)
                    if (choice != Cards.DIPLOMAT && choice != Cards.HORSE_TRADERS && choice != Cards.BEGGAR) {
                        reactions.remove(choice);
                    }
                } else {
                    break;
                }
            } while (reactions.size() > 0);
        }
        return unaffected;
    }

    private Card chooseAttackReaction(Player player, Set<Card> reactions) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card reaction = bot.chooseRevealAttackReaction(reactions);
            Card.checkContains(reactions, reaction, false);
            return reaction;
        }
        return new Prompt(player, this)
                .type(Prompt.Type.REACTION)
                .message("Choose a reaction.")
                .handChoices(reactions)
                .orNone("No reaction")
                .responseCard();
    }

    private Set<Card> getAttackReactions(Player player) {
        Set<Card> reactions = player.getHand().stream()
                .filter(Card::isAttackReaction)
                .collect(Collectors.toSet());
        // diplomat requires a hand of 5 or more cards in order to be revealable
        if (player.getHand().size() < 5) {
            reactions.remove(Cards.DIPLOMAT);
        }
        return reactions;
    }

    private void enterBuyPhase() {
        inBuyPhase = true;
    }

    private void exitBuyPhase() {
        inBuyPhase = false;
    }

    private void cleanup(Player player) {
        // cleanup the player's hand first so cards like Watchtower aren't triggered during cleanup
        player.cleanupHand();
        // handle Scheme
        List<Card> schemed = chooseScheme(player);
        if (!schemed.isEmpty()) {
            message(player, "You put " + Card.htmlList(schemed) + " on top of your deck because of " + Cards.SCHEME.htmlName());
            messageOpponents(player, player.username + " puts " + Card.numCards(schemed.size()) + " on top of their deck because of " + Cards.SCHEME.htmlNameRaw());
            player.putOnDraw(schemed);
        }
        // handle Treasury (and Band of Misfits played as Treasury)
        if (!boughtVictoryCardThisTurn) {
            int numTreasuries = (int) player.getPlay().stream()
                    .filter(c -> c == Cards.TREASURY)
                    .count();
            int numTreasuriesActuallyBandsOfMisfits = (int) player.getPlay().stream()
                    .filter(c -> c instanceof Treasury && c.isBandOfMisfits)
                    .count();
            if (numTreasuries != 0 || numTreasuriesActuallyBandsOfMisfits != 0) {
                // all Treasuries or all Bands of Misfits(played as Treasuries)
                if (numTreasuries == 0 || numTreasuriesActuallyBandsOfMisfits == 0) {
                    boolean areBandsOfMisfits = (numTreasuries == 0);
                    int numToPutOnDeck = chooseNumTreasuriesToPutOnDeck(player, (areBandsOfMisfits ? numTreasuriesActuallyBandsOfMisfits : numTreasuries), areBandsOfMisfits);
                    if (numToPutOnDeck != 0) {
                        if (areBandsOfMisfits) {
                            String cardsStr = Cards.BAND_OF_MISFITS.htmlName(numToPutOnDeck) + " that " + (numToPutOnDeck == 1 ? "was" : "were") + " played as " + Cards.TREASURY.htmlName(numToPutOnDeck);
                            message(player, "You put " + cardsStr + " on top of your deck");
                            messageOpponents(player, player.username + " puts " + cardsStr + " on top of their deck");
                            player.removeFromPlay(player.getPlay().stream().filter(c -> c instanceof Treasury && c.isBandOfMisfits).collect(Collectors.toList()).subList(0, numToPutOnDeck));
                            for (int i = 0; i < numToPutOnDeck; i++) {
                                player.putOnDraw(Cards.BAND_OF_MISFITS);
                            }
                        } else {
                            message(player, "You put " + Cards.TREASURY.htmlName(numToPutOnDeck) + " on top of your deck");
                            messageOpponents(player, player.username + " puts " + Cards.TREASURY.htmlName(numToPutOnDeck) + " on top of their deck");
                            for (int i = 0; i < numToPutOnDeck; i++) {
                                player.removeFromPlay(Cards.TREASURY);
                                player.putOnDraw(Cards.TREASURY);
                            }
                        }
                    }
                } else {
                    // player has both Treasuries and Band of Misfits(played as Treasuries) in play, any of which can be put on top of their deck, in any order
                    while (numTreasuries != 0 || numTreasuriesActuallyBandsOfMisfits != 0) {
                        int choice;
                        if (player instanceof Bot) {
                            choice = ((Bot) player).treasuryPutTreasuryOrBandOfMisfitsOrNoneOnDeck(numTreasuries != 0);
                            if (choice < 0 || choice > 2
                                    || (choice == 0 && numTreasuries == 0)
                                    || (choice == 1 && numTreasuriesActuallyBandsOfMisfits ==0)) {
                                throw new IllegalStateException();
                            }
                        } else {
                            Set<Integer> disabledIndexes = Collections.emptySet();
                            if (numTreasuries == 0) {
                                disabledIndexes = Collections.singleton(0);
                            } else if (numTreasuriesActuallyBandsOfMisfits == 0) {
                                disabledIndexes = Collections.singleton(1);
                            }
                            choice = new Prompt(player, this)
                                    .message("Clean Up: You have Treasuries and Bands of Misfits(played as Treasuries) in play, any of which can be put on top of your deck, in any order.")
                                    .multipleChoices(new String[]{"Put a Treasury on top of your deck", "Put a Band of Misfits on top of your deck", "Done"}, disabledIndexes)
                                    .responseMultipleChoiceIndex();
                        }
                        if (choice == 0) {
                            message(player, "You put " + Cards.TREASURY.htmlName() + " on top of your deck");
                            messageOpponents(player, player.username + " puts " + Cards.TREASURY.htmlName() + " on top of their deck");
                            player.removeFromPlay(Cards.TREASURY);
                            player.putOnDraw(Cards.TREASURY);
                            numTreasuries--;
                        } else if (choice == 1) {
                            message(player, "You put " + Cards.BAND_OF_MISFITS.htmlName() + " that was played as " + Cards.TREASURY.htmlName() + " on top of your deck");
                            messageOpponents(player, player.username + " puts " + Cards.BAND_OF_MISFITS.htmlName() + " that was played as " + Cards.TREASURY.htmlName() + " on top of their deck");
                            Optional<Card> nextBandOfMisfits = player.getPlay().stream()
                                    .filter(c -> c instanceof Treasury && c.isBandOfMisfits)
                                    .findFirst();
                            if (!nextBandOfMisfits.isPresent()) {
                                throw new IllegalStateException();
                            }
                            player.removeFromPlay(nextBandOfMisfits.get());
                            player.putOnDraw(Cards.BAND_OF_MISFITS);
                            numTreasuriesActuallyBandsOfMisfits--;
                        } else { // choice == 2
                            break;
                        }
                    }
                }
            }
        }
        // handle Hermit (and Band of Misfits played as Hermit)
        if (!boughtCardThisTurn) {
            List<Card> hermitsInPlay = player.getPlay().stream()
                    .filter(c -> c instanceof Hermit)
                    .collect(Collectors.toList());
            if (!hermitsInPlay.isEmpty()) {
                int numMadmen = Math.min(hermitsInPlay.size(), nonSupply.get(Cards.MADMAN));
                String hermitsStr = Cards.HERMIT.htmlName(hermitsInPlay.size());
                int numHermitsActuallyBandOfMisfits = (int) hermitsInPlay.stream()
                        .filter(c -> c.isBandOfMisfits)
                        .count();
                if (numHermitsActuallyBandOfMisfits != 0) {
                    hermitsStr += " (" + numHermitsActuallyBandOfMisfits + " of which " + (numHermitsActuallyBandOfMisfits == 1 ? "is" : "are") + " " + Cards.BAND_OF_MISFITS.htmlNameRaw() + ")";
                }
                message(player, "You trash " + hermitsStr + " and gain " + Cards.MADMAN.htmlName(numMadmen));
                messageOpponents(player, player.username + " trashes " + hermitsStr + " and gains " + Cards.MADMAN.htmlName(numMadmen));
                player.removeFromPlay(hermitsInPlay);
                trash(player, hermitsInPlay);
                for (int i = 0; i < numMadmen; i++) {
                    gain(player, Cards.MADMAN);
                }
            }
        }
        // cleanup the rest of play and redraw
        player.cleanupPlay();
        player.turns++;
    }

    private List<Card> chooseScheme(Player player) {
        List<Card> schemeable = player.getPlay().stream()
                .filter(Card::isAction)
                .collect(Collectors.toList());
        if (schemesPlayedThisTurn == 0 || schemeable.isEmpty()) {
            return Collections.emptyList();
        }
        if (player instanceof Bot) {
            List<Card> schemed = ((Bot) player).scheme(Collections.unmodifiableList(schemeable), schemesPlayedThisTurn);
            Card.checkContains(schemeable, schemed);
            return schemed;
        }
        // TODO: prompt in-play
        List<Card> schemed = new ArrayList<>();
        for (int i = 0; i < schemesPlayedThisTurn; i++) {
            String[] choices = new String[schemeable.size() + 1];
            for (int j = 0; j < schemeable.size(); j++) {
                choices[j] = schemeable.get(j).toString();
            }
            choices[choices.length - 1] = "None";
            int choice = new Prompt(player, this)
                    .message(Cards.SCHEME.toString() + ": You may choose " + (schemesPlayedThisTurn - schemed.size()) + " action card(s) in play to put on your deck (the first card you choose will be on top of your deck)")
                    .multipleChoices(choices)
                    .responseMultipleChoiceIndex();
            if (choice == choices.length - 1) {
                // chooose none
                break;
            } else {
                Card toScheme = schemeable.get(choice);
                player.removeFromPlay(toScheme);
                schemeable.remove(toScheme);
                schemed.add(toScheme);
            }
        }
        return schemed;
    }

    private int chooseNumTreasuriesToPutOnDeck(Player player, int max, boolean areBandsOfMisfits) {
        if (player instanceof Bot) {
            int num = ((Bot) player).treasuryNumToPutOnDeck(max);
            Card.checkMultipleChoice(num, max + 1);
            return num;
        }
        String[] choices = new String[max + 1];
        for (int i = 0; i <= max; i++) {
            choices[i] = i + "";
        }
        return new Prompt(player, this)
                .message("Clean Up: Put how many " + (areBandsOfMisfits ? "Bands of Misfits (played as Treasuries)" : "Treasuries") + " on top of your deck?")
                .multipleChoices(choices)
                .responseMultipleChoiceIndex();
    }

    private boolean gameOverConditionMet() {
        // check if game has been forfeited
        if (isGameOver) {
            return true;
        }
        // check if province pile is empty
        if (supply.get(Cards.PROVINCE) == 0) {
            return true;
        }
        // check if colony pile is empty
        if (supply.containsKey(Cards.COLONY) && supply.get(Cards.COLONY) == 0) {
            return true;
        }
        // check if three supply piles are empty
        return numEmptySupplyPiles() >= 3;
    }

    private void announceWinner() {
        boolean tieBroken = false;
        Map<Player, VictoryReportCard> reportCards = new HashMap<>();
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
            message(player, "You had " + victoryPointSummary(player, reportCards.get(player)));
            for (Player opponent : getOpponents(player)) {
                if (opponent.forfeit) {
                    message(player, opponent.username + " forfeited, having " + victoryPointSummary(opponent, reportCards.get(opponent)));
                } else {
                    message(player, opponent.username + " had " + victoryPointSummary(opponent, reportCards.get(opponent)));
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

    private String victoryPointSummary(Player player, VictoryReportCard card) {
        String summary = card.points + " victory points (" + Card.htmlList(card.victoryCards);
        if (player.getVictoryTokens() != 0) {
            summary += ", " + player.getVictoryTokens() + " VP tokens)";
        } else {
            summary += ")";
        }
        return summary;
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
        int points;
        List<Card> victoryCards;

        VictoryReportCard(Player player) {
            List<Card> deck = player.getDeck();
            victoryCards = deck.stream()
                    .filter(c -> c.isVictory() || c == Cards.CURSE)
                    .collect(Collectors.toList());
            points = 0;
            victoryCards.forEach(c -> points += c.victoryValue(deck));
            points += player.getVictoryTokens();
        }
    }

    public Player currentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getOpponents(Player player) {
        List<Player> opponents = new ArrayList<>();
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
        if (prizeCards.contains(card)) {
            prizeCards.remove(card);
        } else if (nonSupply.containsKey(card)) {
            nonSupply.put(card, nonSupply.get(card) - 1);
        } else if (card.inMixedPile()) {
            Card.MixedPileId id = card.mixedPileId();
            mixedPiles.get(id).remove(0);
        } else {
            supply.put(card, supply.get(card) - 1);
        }
    }

    public boolean canReturnToSupply(Card card) {
        if (card.inMixedPile()) {
            return mixedPiles.containsKey(card.mixedPileId());
        }
        return supply.containsKey(card);
    }

    public void returnToSupply(Card card, int count) {
        if (card.inMixedPile()) {
            Card.MixedPileId id = card.mixedPileId();
            for (int i = 0; i < count; i++) {
                mixedPiles.get(id).add(0, card);
            }
        } else {
            supply.put(card, supply.get(card) + count);
        }
    }

    public void returnToNonSupply(Card card) {
        nonSupply.put(card, nonSupply.get(card) + 1);
    }

    private enum GainDestination {DISCARD, DRAW, HAND}

    /**
     * Returns true if the gained card has been replaced, e.g. replaced with a Silver via Trader.
     */
    public boolean gain(Player player, Card card) {
        if (gainReplace(player, card, GainDestination.DISCARD)) {
            return true;
        }
        if (gainRedirect(player, card)) {
            return false;
        }
        takeFromSupply(card);
        // put card in player's discard
        player.addToDiscard(card, false);
        onGained(player, card);
        return false;
    }

    public void gainToTopOfDeck(Player player, Card card) {
        if (gainReplace(player, card, GainDestination.DRAW)) {
            return;
        }
        if (gainRedirect(player, card)) {
            return;
        }
        takeFromSupply(card);
        // put card on top of player's deck
        player.putOnDraw(card);
        onGained(player, card);
    }

    public void gainToHand(Player player, Card card) {
        if (gainReplace(player, card, GainDestination.HAND)) {
            return;
        }
        if (gainRedirect(player, card)) {
            return;
        }
        takeFromSupply(card);
        // put card in player's hand
        player.addToHand(card);
        onGained(player, card);
    }

    public void gainFromTrash(Player player, Card card) {
        gainFromTrash(player, card, false);
    }

    void gainFromTrash(Player player, Card card, boolean toDeck) {
        if (gainReplace(player, card, toDeck ? GainDestination.DRAW : GainDestination.DISCARD)) {
            return;
        }
        if (gainRedirect(player, card)) {
            return;
        }
        removeFromTrash(card);
        if (toDeck) {
            player.putOnDraw(card);
        } else {
            player.addToDiscard(card, false);
        }
        onGained(player, card);
    }

    private boolean gainReplace(Player player, Card card, GainDestination dst) {
        // handle Trader's reaction to replace gained cards with Silver, but only for non-Silvers
        if (card != Cards.SILVER && player.getHand().contains(Cards.TRADER) && chooseRevealTrader(player, card)) {
            messageIndent++;
            if (supply.get(Cards.SILVER) == 0) {
                // if there are no Silvers to gain, gain nothing
                message(player, "revealing " + Cards.TRADER.htmlName() + " and gaining nothing instead");
                return true;
            }
            message(player, "revealing " + Cards.TRADER.htmlName() + " and gaining " + Cards.SILVER.htmlName() + " instead");
            messageIndent--;
            card = Cards.SILVER;
            // still allow the gain to be redirected
            if (gainRedirect(player, card)) {
                return true;
            }
            // if not redirected, put the Silver where the card it replaced was going
            takeFromSupply(card);
            switch (dst) {
                case DISCARD:
                    player.addToDiscard(card, false);
                    break;
                case DRAW:
                    player.putOnDraw(card);
                    break;
                default: // HAND
                    player.addToHand(card);
            }
            onGained(player, card);
            return true;
        }
        return false;
    }

    private boolean chooseRevealTrader(Player player, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).traderReplaceWithSilver(card);
        }
        int choice = new Prompt(player, this)
                .type(Prompt.Type.REACTION)
                .message(Cards.TRADER.toString() + ": Reveal " + Cards.TRADER.htmlName() + " and gain " + Cards.SILVER.htmlName() + " instead of " + card.htmlName() + "?")
                .multipleChoices(new String[]{"Yes", "No"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private boolean gainRedirect(Player player, Card card) {
        if (player.getHand().contains(Cards.WATCHTOWER)) {
            int choice = chooseWatchtowerTrashOrPutOnDeckOrNone(player, card);
            if (choice != 2) {
                messageIndent++;
                message(player, "you reveal " + Cards.WATCHTOWER.htmlName());
                messageOpponents(player, player.username + " reveals " + Cards.WATCHTOWER.htmlName());
                messageIndent++;
                if (choice == 0) {
                    messageAll("trashing the " + card.htmlNameRaw());
                    takeFromSupply(card);
                    trash(player, card);
                } else {
                    message(player, "putting the " + card.htmlNameRaw() + " on top of your deck");
                    messageOpponents(player, "putting the " + card.htmlNameRaw() + " on top of their deck");
                    takeFromSupply(card);
                    player.putOnDraw(card);
                    onGained(player, card);
                }
                messageIndent--;
                messageIndent--;
                return true;
            }
        }
        if (card == Cards.NOMAD_CAMP) {
            messageIndent++;
            message(player, "putting it on top of your deck");
            messageOpponents(player, "putting it on top of their deck");
            takeFromSupply(card);
            player.putOnDraw(card);
            onGained(player, card);
            messageIndent--;
            return true;
        }
        if (player.getPlay().contains(Cards.ROYAL_SEAL)) {
            if (chooseRoyalSealPutOnDeck(player, card)) {
                messageIndent++;
                message(player, "you use your " + Cards.ROYAL_SEAL.htmlNameRaw() + " to put the " + card.htmlNameRaw() + " on top of your deck");
                messageOpponents(player, player.username + " uses their " + Cards.ROYAL_SEAL.htmlNameRaw() + " to put the " + card.htmlNameRaw() + " on top of their deck");
                takeFromSupply(card);
                player.putOnDraw(card);
                onGained(player, card);
                messageIndent--;
                return true;
            }
        }
        return false;
    }

    private int chooseWatchtowerTrashOrPutOnDeckOrNone(Player player, Card card) {
        if (player instanceof Bot) {
            int choice = ((Bot) player).watchtowerTrashOrPutOnDeckOrNone(card);
            Card.checkMultipleChoice(3, choice);
            return choice;
        }
        return new Prompt(player, this)
                .type(Prompt.Type.REACTION)
                .message("You gained " + card.htmlName() + ". You may reveal a " + Cards.WATCHTOWER.htmlNameRaw() + " to trash it or put it on top of your deck.")
                .multipleChoices(new String[] {"Trash", "Put on deck", "Do nothing"})
                .responseMultipleChoiceIndex();
    }

    private boolean chooseRoyalSealPutOnDeck(Player player, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).royalSealPutOnDeck(card);
        }
        int choice = new Prompt(player, this)
                .message(Cards.ROYAL_SEAL.toString() + ": Put the " + card.htmlNameRaw() + " on top of your deck?")
                .multipleChoices(new String[] {"Put on deck", "Don't"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private void onGained(Player player, Card card) {
        messageIndent++;
        player.cardsGainedDuringTurn.add(card);
        if (tradeRouteTokenedPiles.contains(card)) {
            tradeRouteTokenedPiles.remove(card);
            tradeRouteMat++;
            messageAll("the trade route token on " + card.htmlNameRaw() + " moves to the trade route mat");
        }
        // handle Duchess effect on gaining a Duchy
        if (card == Cards.DUCHY && supply.containsKey(Cards.DUCHESS) && supply.get(Cards.DUCHESS) != 0) {
            if (chooseGainDuchessOnGainingDuchy(player)) {
                messageAll("gaining " + Cards.DUCHESS.htmlName());
                gain(player, Cards.DUCHESS);
            }
        }
        // handle Fool's Gold effect on gaining a Province
        if (card == Cards.PROVINCE) {
            for (Player opponent : getOpponents(player)) {
                int numFoolsGolds = opponent.numberInHand(Cards.FOOLS_GOLD);
                for (int i = 0; i < numFoolsGolds; i++) {
                    if (chooseRevealFoolsGold(opponent)) {
                        if (supply.get(Cards.GOLD) != 0) {
                            message(opponent, "You trash " + Cards.FOOLS_GOLD.htmlName() + " and gain " + Cards.GOLD + " onto your deck");
                            messageOpponents(opponent, opponent.username + " trashes " + Cards.FOOLS_GOLD.htmlName() + " and gains " + Cards.GOLD + " onto their deck");
                            player.removeFromHand(Cards.FOOLS_GOLD);
                            trash(player, Cards.FOOLS_GOLD);
                            gainToTopOfDeck(opponent, Cards.GOLD);
                        } else {
                            message(opponent, "You trash " + Cards.FOOLS_GOLD.htmlName() + " and gain nothing");
                            messageOpponents(opponent, opponent.username + " trashes " + Cards.FOOLS_GOLD.htmlName() + " and gains nothing");
                            player.removeFromHand(Cards.FOOLS_GOLD);
                            trash(player, Cards.FOOLS_GOLD);
                        }
                    } else {
                        // if the player stops trashing Fool's Golds, stop asking, even if they have more Fool's Golds
                        break;
                    }
                }
            }
        }
        card.onGain(player, this);
        messageIndent--;
    }

    private boolean chooseGainDuchessOnGainingDuchy(Player player) {
        if (player instanceof Bot) {
            return ((Bot) player).duchessGainDuchessOnGainingDuchy();
        }
        int choice = new Prompt(player, this)
                .message(Cards.DUCHESS.toString() + ": Gain " + Cards.DUCHESS.htmlName() + "?")
                .multipleChoices(new String[]{"Gain Duchess", "Don't"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private boolean chooseRevealFoolsGold(Player player) {
        if (player instanceof Bot) {
            return ((Bot) player).foolsGoldReveal();
        }
        int choice = new Prompt(player, this)
                .type(Prompt.Type.REACTION)
                .message(Cards.FOOLS_GOLD.toString() + ": Trash " + Cards.FOOLS_GOLD.htmlName() + " and gain " + Cards.GOLD.htmlName() + " onto your deck?")
                .multipleChoices(new String[]{"Yes", "No"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    public List<Card> getTrash() {
        return trash;
    }

    public void trash(Player player, Card card) {
        trash(player, card, true);
    }

    public void trash(Player player, List<Card> cards) {
        cards.forEach(c -> trash(player, c, true));
    }

    public void trash(Player player, Card card, boolean triggersMarketSquare) {
        if (onTrash(player, card, triggersMarketSquare)) {
            card = card.isBandOfMisfits ? Cards.BAND_OF_MISFITS : card;
            trash.add(card);
        }
    }

    private boolean onTrash(Player player, Card card, boolean triggersMarketSquare) {
        boolean isTrashed;
        messageIndent++;
        if (triggersMarketSquare) {
            // Market Square can react before or after the trashed card's on-trash effect
            allowMarketSquareReaction(player);
            isTrashed = card.onTrashIsTrashed(player, this);
            allowMarketSquareReaction(player);
        } else {
            isTrashed = card.onTrashIsTrashed(player, this);
        }
        messageIndent--;
        return isTrashed;
    }

    private void allowMarketSquareReaction(Player player) {
        while (player.getHand().contains(Cards.MARKET_SQUARE)) {
            if (!((MarketSquare) Cards.MARKET_SQUARE).onCardTrashed(player, this)) {
                break;
            }
        }
    }

    private void removeFromTrash(Card card) {
        trash.remove(card);
    }

    // TODO: rewrite
    private boolean hasUnplayedTreasure(Player player) {
        for (Card card : player.getHand()) {
            if (card.isTreasure()) {
                return true;
            }
        }
        return false;
    }

    private Set<Card> buyableCards(Player player) {
        int usableCoins = player.getUsableCoins();
        Set<Card> buyable = cardsCostingAtMost(usableCoins);
        // remove cards prohibited by contraband
        buyable.removeAll(contrabandProhibited);
        // remove grand market if the player has a copper in play
        if (buyable.contains(Cards.GRAND_MARKET) && player.getPlay().contains(Cards.COPPER)) {
            buyable.remove(Cards.GRAND_MARKET);
        }
        return buyable;
    }

    private Set<Card> playableTreasures(Player player) {
        return player.getHand().stream()
                .filter(Card::isTreasure)
                .collect(Collectors.toSet());
    }

    public Set<Card> cardsCostingExactly(int cost) {
        return cardsInSupply().stream()
                .filter(c -> c.cost(this) == cost)
                .collect(Collectors.toSet());
    }

    public Set<Card> cardsCostingAtMost(int cost) {
        return cardsInSupply().stream()
                .filter(c -> c.cost(this) <= cost)
                .collect(Collectors.toSet());
    }

    public Set<Card> cardsInSupply() {
        Set<Card> cards = new HashSet<>();
        for (Map.Entry<Card, Integer> pile : supply.entrySet()) {
            Card card = pile.getKey();
            Integer count = pile.getValue();
            if (count != 0) {
                cards.add(card);
            }
        }
        mixedPiles.values().stream()
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .forEach(cards::add);
        return cards;
    }

    int numberInPlay(Card card) {
        return currentPlayer().numberInPlay(card);
    }

    public int numEmptySupplyPiles() {
        int numEmptyPiles = 0;
        for (Integer count : supply.values()) {
            if (count == 0) {
                numEmptyPiles++;
            }
        }
        for (List<Card> mixedPile : mixedPiles.values()) {
            if (mixedPile.isEmpty()) {
                numEmptyPiles++;
            }
        }
        return numEmptyPiles;
    }

    private Set<Card> allCardsInGame() {
        Set<Card> allCards = new HashSet<>();
        // cards in normal supply piles
        allCards.addAll(supply.keySet());
        // cards in mixed supply piles
        mixedPiles.values().forEach(allCards::addAll);
        // cards in non-supply piles
        allCards.addAll(nonSupply.keySet());
        // prize cards
        allCards.addAll(prizeCards);
        // shelters
        if (usingShelters) {
            allCards.addAll(Cards.SHELTER_CARDS);
        }
        return allCards;
    }

    void forfeit(Player player, boolean connectionClosed) {
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
    }

    public void addEmbargoToken(Card.MixedPileId pileId) {
        mixedPileEmbargoTokens.put(pileId, mixedPileEmbargoTokens.get(pileId) + 1);
    }

    private static final int SECONDS_BEFORE_HURRY_UP = 20;
    private static final int SECONDS_AFTER_HURRY_UP = 40;
    private Boolean canHurryUp = false;
    private Player toHurryUp;
    private boolean hurryingUp;
    private long hurryUpStartTime;

    Object sendPromptAndGetResponse(Player player, Prompt prompt) {
        player.issueCommand(prompt.toJSON());
        return waitForResponse(player);
    }

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

    void hurryUp(Player sender) {
        if (canHurryUp && !hurryingUp) {
            hurryingUp = true;
            hurryUpStartTime = System.currentTimeMillis();
            for (Player player : players) {
                if (player == toHurryUp) {
                    message(player, "<span class=\"hurryUp\">" + sender.username + " asks you to hurry up! You will automatically forfeit in " + SECONDS_AFTER_HURRY_UP + " seconds!</span>");
                } else if (player == sender) {
                    message(player, "<span class=\"hurryUp\">" + "You ask " + toHurryUp.username + " to hurry up! They will automatically forfeit in " + SECONDS_AFTER_HURRY_UP + " seconds!</span>");
                } else {
                    message(player, "<span class=\"hurryUp\">" + sender.username + " asks " + toHurryUp.username + " to hurry up! They will automatically forfeit in " + SECONDS_AFTER_HURRY_UP + " seconds!</span>");
                }
            }
            issueCommandsToAllPlayers();
        }
    }

    private static class BuyPhaseChoice {
        Card toBuy;
        Card toPlay;
        boolean isPlayingAllTreasures;
        int coinTokensToSpend;
        boolean isEndingTurn;
    }

    private BuyPhaseChoice promptBuyPhase(Player player) {
        return promptBuyPhase(player, buyableCards(player), playableTreasures(player));
    }

    private BuyPhaseChoice promptBuyPhase(Player player, Set<Card> canBuy, Set<Card> canPlay) {
        BuyPhaseChoice choice = new BuyPhaseChoice();
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            // have the bot automatically play all of its treasures at the beginning of its turn
            if (!canPlay.isEmpty()) {
                choice.isPlayingAllTreasures = true;
                return choice;
            }
            Card card = bot.chooseBuy(canBuy);
            Card.checkContains(canBuy, card, false);
            // if the bot doesn't want to buy anything, end its turn
            if (card == null) {
                choice.isEndingTurn = true;
            } else {
                choice.toBuy = card;
            }
            return choice;
        }
        String PLAY_ALL_TREASURES = "Play all treasures";
        String SPEND_COIN_TOKENS = "Spend coin tokens";
        String END_TURN = "End turn";
        List<String> multipleChoiceList = new ArrayList<>();
        if (!canPlay.isEmpty()) {
            multipleChoiceList.add(PLAY_ALL_TREASURES);
        }
        if (player.getCoinTokens() != 0) {
            multipleChoiceList.add(SPEND_COIN_TOKENS);
        }
        multipleChoiceList.add(END_TURN);
        String[] multipleChoices = new String[multipleChoiceList.size()];
        for (int i = 0; i < multipleChoiceList.size(); i++) {
            multipleChoices[i] = multipleChoiceList.get(i);
        }
        PromptResponse response = new Prompt(player, this)
                .type(Prompt.Type.BUY)
                .message("Buy Phase: Buy cards from the supply, or play treasures from your hand.")
                .handChoices(canPlay)
                .supplyChoices(canBuy)
                .multipleChoices(multipleChoices)
                .response();
        if (response.handChoice != null) {
            choice.toPlay = response.handChoice;
        } else if (response.supplyChoice != null) {
            choice.toBuy = response.supplyChoice;
        } else {
            String multipleChoice = multipleChoiceList.get(response.multipleChoiceIndex);
            if (multipleChoice.equals(PLAY_ALL_TREASURES)) {
                choice.isPlayingAllTreasures = true;
            } else if (multipleChoice.equals(SPEND_COIN_TOKENS)) {
                String[] coinTokenChoices = new String[player.getCoinTokens()];
                for (int i = 0; i < player.getCoinTokens(); i++) {
                    coinTokenChoices[i] = (i + 1) + "";
                }
                int coinTokenChoice = new Prompt(player, this)
                        .type(Prompt.Type.BUY)
                        .message("Buy Phase: Spend how many coin tokens?")
                        .multipleChoices(coinTokenChoices)
                        .responseMultipleChoiceIndex();
                choice.coinTokensToSpend = coinTokenChoice + 1;
            } else { // multipleChoice.equals(END_TURN)
                choice.isEndingTurn = true;
            }
        }
        return choice;
    }

    Card promptChoosePlay(Player player, Set<Card> playable, String promptMessage) {
        return promptChoosePlay(player, playable, promptMessage, null);
    }

    Card promptChoosePlay(Player player, Set<Card> playable, String promptMessage, String noneMessage) {
        boolean isMandatory = (noneMessage == null);
        if (player instanceof Bot) {
            Card toPlay = ((Bot) player).choosePlay(playable, isMandatory);
            Card.checkContains(playable, toPlay, isMandatory);
            return toPlay;
        }
        Prompt prompt = new Prompt(player, this)
                .type(Prompt.Type.PLAY)
                .message(promptMessage)
                .handChoices(playable);
        if (!isMandatory) {
            prompt.orNone(noneMessage);
        }
        return prompt.responseCard();
    }

    Set<Card> cardsChoosableInSupplyUI() {
        // add any card with a uniform pile (these can be chosen even when empty)
        Set<Card> cards = new HashSet<>(supply.keySet());
        // add any top card of a mixed pile
        mixedPiles.values().stream()
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .forEach(cards::add);
        return cards;
    }

    @SuppressWarnings("unchecked")
    private void newTurnMessage(Player player, String str) {
        JSONObject command = new JSONObject();
        command.put("command", "newTurnMessage");
        command.put("text", str);
        player.sendCommand(command);
    }

    private void newTurnMessage(Player player) {
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

    private List<Object> savedResponses = new ArrayList<>();
    private boolean isReplayingSavedResponses = false;
    private int nextSavedResponseIndex;

    @SuppressWarnings("unchecked")
    public <T> T prompt(Player player, Function<Bot, T> botPrompt, Supplier<T> playerPrompt) {
        if (isReplayingSavedResponses) {
            T response = (T) savedResponses.get(nextSavedResponseIndex);
            nextSavedResponseIndex++;
            return response;
        }
        T response;
        if (player instanceof Bot) {
            response = botPrompt.apply((Bot) player);
        } else {
            response = playerPrompt.get();
        }
        savedResponses.add(response);
        return response;
    }

    // record player choices for later data mining
    private Map<Player, List<Card>> gainRecords;

    private void initRecords() {
        gainRecords = new HashMap<>();
        for (Player player : players) {
            gainRecords.put(player, new ArrayList<>());
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
            server.recordWinningStrategy(kingdomCards, gainRecord);
        }
    }

}
