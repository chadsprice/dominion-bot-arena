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
import util.PeekableIterator;

public class Player {

    public static final Comparator<Card> HAND_ORDER_COMPARATOR =
            (a, b) -> {
                int a_type = HAND_ORDER_TYPE(a);
                int b_type = HAND_ORDER_TYPE(b);
                if (a_type != b_type) {
                    return a_type - b_type;
                } else {
                    // coins and victories
                    if (a_type == 2 || b_type == 3) {
                        // order by highest cost, which is useful for upgrading
                        return b.cost() - a.cost();
                    }
                    // order alphabetically
                    return a.toString().compareTo(b.toString());
                }
            };

    private static int HAND_ORDER_TYPE(Card card) {
        // action < treasure < victory < curse
        if (card.isAction()) {
            return 0;
        } else if (card.isTreasure()) {
            return 1;
        } else if (card.isVictory()) {
            return 2;
        } else {
            return 3;
        }
    }

    PlayerWebSocketHandler conn;
    private List<JSONObject> commands = new ArrayList<>();

    BlockingQueue<Object> responses = new LinkedBlockingQueue<>();
    boolean forfeit;

    void sendCommand(JSONObject command) {
        sendCommand(command, false);
    }

    void issueCommand(JSONObject command) {
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
        if (forfeit || commands.isEmpty()) {
            return;
        }

        if (game != null) {
            GameView gameView = new GameView(this, game);
            JSONObject gameViewUpdate;
            if (previousGameView == null) {
                gameViewUpdate = gameView.completeUpdate();
            } else {
                gameViewUpdate = gameView.computeUpdate(previousGameView);
            }
            // TODO: insert before prompts and waiting-for-opponent, don't just insert before the last command
            commands.add(Math.max(0, commands.size() - 1), gameViewUpdate);
            previousGameView = gameView;
        }

        JSONArray commandArray = new JSONArray();
        commandArray.addAll(commands);
        commands.clear();
        conn.send(commandArray.toJSONString());
    }

    void receiveResponse(Object response) {
        responses.add(response);
    }

    public String username;

    public Game game;

    private List<Card> draw = new ArrayList<>();
    List<Card> hand = new ArrayList<>();
    private List<Card> play = new ArrayList<>();
    private List<Card> discard = new ArrayList<>();

    List<Card> nativeVillageMat = new ArrayList<>();
    List<Card> islandMat = new ArrayList<>();
    private int pirateShipTokens = 0;

    private int coinTokens = 0;
    private int victoryTokens = 0;

    private List<DurationEffect> durationEffects = new ArrayList<>();
    List<Card> durationSetAsideCards = new ArrayList<>();
    private List<Card> resolvedDurationCards = new ArrayList<>();

    public int actions;
    public int buys;
    public int coins;

    public int turns;

    // keep track of gained cards for Smugglers
    public Set<Card> cardsGainedDuringTurn = new HashSet<>();

    // keep track of Fool's Golds played
    public boolean playedFoolsGoldThisTurn;

    GameView previousGameView;

    public Player(PlayerWebSocketHandler conn) {
        this.conn = conn;
    }

    void newGameSetup(boolean usingShelters) {
        previousGameView = null;

        draw.clear();
        hand.clear();
        play.clear();
        discard.clear();
        nativeVillageMat.clear();
        islandMat.clear();
        pirateShipTokens = 0;
        coinTokens = 0;
        victoryTokens = 0;
        durationEffects.clear();
        durationSetAsideCards.clear();
        resolvedDurationCards.clear();
        turns = 0;
        cardsGainedDuringTurn.clear();
        if (usingShelters) {
            draw.add(Cards.HOVEL);
            draw.add(Cards.NECROPOLIS);
            draw.add(Cards.OVERGROWN_ESTATE);
        } else {
            for (int i = 0; i < 3; i++) {
                draw.add(Cards.ESTATE);
            }
        }
        for (int i = 0; i < 7; i++) {
            draw.add(Cards.COPPER);
        }
        Collections.shuffle(draw);
        newTurn();
    }

    private void newTurn() {
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
        return durationEffects.stream().anyMatch(effect -> effect.card == Cards.OUTPOST);
    }

    public boolean isTakingExtraTurn() {
        return resolvedDurationCards.contains(Cards.OUTPOST);
    }

    void startNewTurn() {
        cardsGainedDuringTurn.clear();
    }

    void cleanupHand() {
        discard.addAll(hand);
        hand.clear();
    }

    void cleanupPlay() {
        play = play.stream().map(c -> c.isBandOfMisfits ? Cards.BAND_OF_MISFITS : c).collect(Collectors.toList());
        discard.addAll(play);
        play.clear();
        resolvedDurationCards = resolvedDurationCards.stream().map(c -> c.isBandOfMisfits ? Cards.BAND_OF_MISFITS : c).collect(Collectors.toList());
        discard.addAll(resolvedDurationCards);
        resolvedDurationCards.clear();
        newTurn();
    }

    // TODO: rewrite
    boolean hasPlayableAction() {
        for (Card card : hand) {
            if (card.isAction()) {
                return true;
            }
        }
        return false;
    }

    int getUsableCoins() {
        if (!isAutoplayingTreasures()) {
            return coins;
        } else {
            // start with current coins
            int usableCoins = coins;
            int numTreasuresInPlay = 0;
            int numBanks = 0;
            int numFoolsGolds = 0;
            // add the value of all unplayed non-bank, non-fools-gold treasures
            for (Card card : hand) {
                if (card == Cards.BANK) {
                    numBanks++;
                } else if (card.isTreasure()) {
                    if (card == Cards.FOOLS_GOLD) {
                        numFoolsGolds++;
                    } else {
                        usableCoins += card.treasureValue(game);
                    }
                    numTreasuresInPlay++;
                }
            }
            // add the number of treasures currently in play to the number of non-bank treasures
            for (Card card : play) {
                if (card.isTreasure()) {
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
            if (play.contains(Cards.MERCHANT) && !game.playedSilverThisTurn && hand.contains(Cards.SILVER)) {
                usableCoins += numberInPlay(Cards.MERCHANT);
            }
            // handle Diadem +$1 per unused action
            if (hand.contains(Cards.DIADEM)) {
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

    boolean isAutoplayingTreasures() {
        return !hand.contains(Cards.QUARRY) &&
                !hand.contains(Cards.CONTRABAND) &&
                !hand.contains(Cards.VENTURE) &&
                !(game.supply.containsKey(Cards.GRAND_MARKET) && hand.contains(Cards.COPPER)) &&
                !hand.contains(Cards.HORN_OF_PLENTY) &&
                !hand.contains(Cards.ILL_GOTTEN_GAINS) &&
                !hand.contains(Cards.SPOILS) && !hand.contains(Cards.COUNTERFEIT);
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

    boolean handContains(List<Card> cards) {
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
    List<Card> handOrder = new ArrayList<>();

    private void resetHandOrder() {
        handOrder.clear();
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public void addToHand(Card card, int count) {
        for (int i = 0; i < count; i++) {
            hand.add(card);
        }
    }

    public void addToHand(List<Card> cards) {
        hand.addAll(cards);
    }

    public void removeFromHand(Card card) {
        hand.remove(card);
    }

    public void removeFromHand(List<Card> cards) {
        for (Card card : cards) {
            hand.remove(card);
        }
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
        card = card.isBandOfMisfits ? Cards.BAND_OF_MISFITS : card;
        if (triggersTunnelReaction && card == Cards.TUNNEL) {
            handleDiscardedTunnels(1);
        }
        discard.add(card);
    }

    public void addToDiscard(List<Card> cards) {
        addToDiscard(cards, true);
    }

    public void addToDiscard(List<Card> cards, boolean triggersTunnelReaction) {
        cards = cards.stream()
                .map(c -> c.isBandOfMisfits ? Cards.BAND_OF_MISFITS : c)
                .collect(Collectors.toList());
        if (triggersTunnelReaction && cards.contains(Cards.TUNNEL)) {
            int numTunnels = (int) cards.stream()
                    .filter(c -> c == Cards.TUNNEL)
                    .count();
            handleDiscardedTunnels(numTunnels);
        }
        discard.addAll(cards);
    }

    private void handleDiscardedTunnels(int numTunnels) {
        game.messageIndent++;
        for (int i = 0; i < numTunnels && game.supply.get(Cards.GOLD) != 0; i++) {
            if (chooseRevealTunnel(this)) {
                game.messageAll("revealing the " + Cards.TUNNEL.htmlNameRaw() + " and gaining " + Cards.GOLD.htmlName());
                game.gain(this, Cards.GOLD);
            } else {
                break;
            }
        }
        game.messageIndent--;
    }

    private boolean chooseRevealTunnel(Player player) {
        if (player instanceof Bot) {
            return ((Bot) player).tunnelReveal();
        }
        int choice = new Prompt(player, game)
                .type(Prompt.Type.REACTION)
                .message(Cards.TUNNEL.toString() + ": Reveal the " + Cards.TUNNEL.htmlNameRaw() + " and gain " + Cards.GOLD.htmlName() + "?")
                .multipleChoices(new String[]{"Yes", "No"})
                .responseMultipleChoiceIndex();
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
    }

    public void putOnDraw(List<Card> cards) {
        draw.addAll(0, cards);
    }

    public void shuffleIntoDraw(List<Card> cards) {
        draw.addAll(cards);
        Collections.shuffle(draw);
    }

    public List<Card> takeFromDraw(int n) {
        List<Card> drawn = new ArrayList<>();
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
        }
    }

    public List<Card> drawIntoHand(int n) {
        List<Card> drawn = takeFromDraw(n);
        hand.addAll(drawn);
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
    }

    public void removeFromPlay(Card card) {
        play.remove(card);
    }

    public void removeFromPlay(List<Card> cards) {
        for (Card card : cards) {
            play.remove(card);
        }
    }

    List<Card> removeAllTreasuresFromPlay() {
        List<Card> treasures = new ArrayList<>();
        for (Iterator<Card> iter = play.iterator(); iter.hasNext(); ) {
            Card card = iter.next();
            if (card.isTreasure()) {
                iter.remove();
                treasures.add(card);
            }
        }
        return treasures;
    }

    public List<Card> allCardsInPlay() {
        List<Card> cardsInPlay = new ArrayList<>();
        cardsInPlay.addAll(play);
        // TODO: is this correct? what if you Haven another Duration?
        // for Horn of Plenty, duration cards played last turn count as in play (but not their modifiers!)
        resolvedDurationCards.stream()
                .filter(Card::isDuration)
                .forEach(cardsInPlay::add);
        return cardsInPlay;
    }

    public void removeFromDiscard(List<Card> cards) {
        for (Card card : cards) {
            discard.remove(card);
        }
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
    }

    // TODO: remove
    public int getCoinTokens() {
        return coinTokens;
    }
    private void setCoinTokens(int coinTokens) {
        this.coinTokens = coinTokens;
    }
    public void addCoinTokens(int toAdd) {
        setCoinTokens(coinTokens + toAdd);
    }

    // TODO: remove
    public int getPirateShipTokens() {
        return pirateShipTokens;
    }
    public void addPirateShipToken() {
        pirateShipTokens++;
    }

    // TODO: remove
    int getVictoryTokens() {
        return victoryTokens;
    }
    public void addVictoryTokens(int numTokens) {
        victoryTokens += numTokens;
    }

    public void putOnNativeVillageMat(Card card) {
        nativeVillageMat.add(card);
    }

    public List<Card> takeAllFromNativeVillageMat() {
        List<Card> taken = new ArrayList<>(nativeVillageMat);
        nativeVillageMat.clear();
        return taken;
    }

    public void putOnIslandMat(Card card) {
        card = card.isBandOfMisfits ? Cards.BAND_OF_MISFITS : card;
        islandMat.add(card);
    }

    List<DurationEffect> getDurationEffects() {
        return durationEffects;
    }

    public void addDurationEffect(Card card) {
        addDurationEffect(card, null);
    }

    void addDurationEffect(Card card, List<Card> havenedCards) {
        DurationEffect effect = new DurationEffect();
        effect.card = card;
        effect.havenedCards = havenedCards;
        if (havenedCards != null) {
            durationSetAsideCards.addAll(havenedCards);
        }
        durationEffects.add(effect);
    }

    List<Card> getDurationSetAsideCards() {
        return durationSetAsideCards;
    }

    void addDurationSetAside(Card card) {
        durationSetAsideCards.add(card);
    }

    public void removeDurationSetAside(Card card) {
        durationSetAsideCards.remove(card);
    }

    void cleanupDurations() {
        durationEffects.clear();
        resolvedDurationCards.addAll(durationSetAsideCards);
        durationSetAsideCards.clear();
    }

    public List<Card> getDeck() {
        List<Card> deck = new ArrayList<>();
        deck.addAll(draw);
        deck.addAll(hand);
        deck.addAll(play);
        deck.addAll(discard);
        deck.addAll(nativeVillageMat);
        deck.addAll(islandMat);
        deck.addAll(durationSetAsideCards);
        deck.addAll(resolvedDurationCards);
        // count any Band of Misfits still in play as just a Band of Misfits, not as the card it is emulating
        deck = deck.stream()
                .map(c -> c.isBandOfMisfits ? Cards.BAND_OF_MISFITS : c)
                .collect(Collectors.toList());
        return deck;
    }

}
