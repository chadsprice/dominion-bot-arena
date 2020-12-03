package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.PeekableIterator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
class GameView {

    private class PileView {
        String topCard;
        int cost;
        int size;
        int embargoTokens;
        boolean hasTradeRouteToken;
    }

    private class OpponentView {
        String username;
        int handSize;
        int drawSize;
        int discardSize;
        int victoryPoints;
        List<Count> durations;
        List<Count> inPlay;
    }

    private static class Count {
        String card;
        int count;

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Count)) {
                return false;
            }
            Count otherCount = (Count) other;
            return otherCount.card.equals(card)
                    && otherCount.count == count;
        }

        @Override
        public int hashCode() {
            return Objects.hash(card, count);
        }
    }

    private Map<String, PileView> pileViews;
    private Map<String, Boolean> prizeCards;
    private List<Count> trash;
    private int tradeRoute;

    private List<OpponentView> opponentViews;
    //String waitingOn; TODO

    private int drawSize;
    private int discardSize;
    private String actions;
    private String buys;
    private int coins;
    private boolean isAutoplayingTreasures;
    private int coinTokens;
    private int pirateShip;
    private int victoryTokens;
    private int victoryPoints;
    private List<Count> nativeVillage;
    private List<Count> island;
    private List<Count> durations;
    private List<Count> inPlay;
    private List<Count> hand;

    GameView(Player player, Game game) {
        pileViews = new HashMap<>();
        for (Card card : game.supply.keySet()) {
            PileView pileView = new PileView();
            pileView.topCard = card.toString();
            pileView.cost = card.cost(game);
            pileView.size = game.supply.get(card);
            pileView.embargoTokens = game.embargoTokens.get(card);
            pileView.hasTradeRouteToken = game.tradeRouteTokenedPiles.contains(card);
            pileViews.put(card.toString(), pileView);
        }
        for (Card card : game.nonSupply.keySet()) {
            PileView pileView = new PileView();
            pileView.topCard = card.toString();
            pileView.cost = card.cost(game);
            pileView.size = game.nonSupply.get(card);
            pileView.embargoTokens = 0;
            pileView.hasTradeRouteToken = false;
            pileViews.put(card.toString(), pileView);
        }
        for (Card.MixedPileId mixedPile : game.mixedPiles.keySet()) {
            PileView pileView = new PileView();
            List<Card> mixedPileCards = game.mixedPiles.get(mixedPile);
            if (mixedPileCards.isEmpty()) {
                pileView.topCard = "";
                pileView.cost = -1;
            } else {
                Card topCard = mixedPileCards.get(0);
                pileView.topCard = topCard.toString();
                pileView.cost = topCard.cost(game);
            }
            pileView.size = mixedPileCards.size();
            pileView.embargoTokens = game.mixedPileEmbargoTokens.get(mixedPile);
            pileView.hasTradeRouteToken = false; // mixed piles cannot have trade route tokens (in the current implementation)
            pileViews.put(mixedPile.toString(), pileView);
        }

        if (game.supply.containsKey(Cards.TOURNAMENT)) {
            prizeCards = Cards.PRIZE_CARDS.stream()
                    .collect(Collectors.toMap(Card::toString, game.prizeCards::contains));
        } else {
            prizeCards = Collections.emptyMap();
        }

        trash = counts(game.trash);
        tradeRoute = game.tradeRouteMat;

        opponentViews = game.getOpponents(player).stream()
                .map(opponent -> {
                    OpponentView opponentView = new OpponentView();
                    opponentView.username = opponent.username;
                    opponentView.handSize = opponent.getHand().size();
                    opponentView.drawSize = opponent.getDraw().size();
                    opponentView.discardSize = opponent.getDiscard().size();
                    opponentView.victoryPoints = victoryPoints(opponent);
                    opponentView.durations = counts(opponent.getDurationSetAsideCards());
                    opponentView.inPlay = adjacentCounts(opponent.getPlay());
                    return opponentView;
                }).collect(Collectors.toList());

        drawSize = player.getDraw().size();
        discardSize = player.getDiscard().size();
        if (game.currentPlayer() == player && !game.inBuyPhase) {
            actions = "" + player.actions;
        } else {
            actions = "";
        }
        if (game.currentPlayer() == player) {
            buys = "" + player.buys;
        } else {
            buys = "";
        }
        coins = player.getUsableCoins();
        isAutoplayingTreasures = player.isAutoplayingTreasures();
        coinTokens = player.getCoinTokens();
        pirateShip = player.getPirateShipTokens();
        victoryTokens = player.getVictoryTokens();
        victoryPoints = victoryPoints(player);
        nativeVillage = counts(player.nativeVillageMat);
        island = counts(player.islandMat);
        durations = counts(player.durationSetAsideCards);
        inPlay = adjacentCounts(player.getPlay());
        hand = handCounts(player);
    }

    private static List<Count> counts(List<Card> cards) {
        // group cards by name and count each group
        Map<Card, Long> longCounts = cards.stream()
                .collect(Collectors.groupingBy(Card::toString, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(e -> Cards.fromName(e.getKey()), Map.Entry::getValue));
        // sort groups
        List<Card> sorted = new ArrayList<>(longCounts.keySet());
        sorted.sort(Player.HAND_ORDER_COMPARATOR);
        // return as counts
        return sorted.stream()
                .map(card -> {
                    Count count = new Count();
                    count.card = card.toString();
                    count.count = longCounts.get(card).intValue();
                    return count;
                }).collect(Collectors.toList());
    }

    private static List<Count> adjacentCounts(List<Card> cards) {
        List<Count> counts = new ArrayList<>();
        for (PeekableIterator<Card> iter = new PeekableIterator<>(cards); iter.hasNext(); ) {
            Card card = iter.next();
            int count = 1;
            while (iter.hasNext() && iter.peek() == card) {
                iter.next();
                count++;
            }
            Count cardCount = new Count();
            cardCount.card = card.toString();
            cardCount.count = count;
            counts.add(cardCount);
        }
        return counts;
    }

    private static List<Count> handCounts(Player player) {
        // group cards by name and count each group
        Map<Card, Long> longCounts = player.hand.stream()
                .collect(Collectors.groupingBy(Card::toString, Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.toMap(e -> Cards.fromName(e.getKey()), Map.Entry::getValue));
        // remove cards from the hand order that are no longer in the hand
        player.handOrder.removeIf(card -> !longCounts.containsKey(card));
        // add cards to the end of the hand order that were not in the hand before
        Set<Card> newCards = new HashSet<>(longCounts.keySet());
        player.handOrder.forEach(newCards::remove);
        List<Card> newCardsOrdered = new ArrayList<>(newCards);
        newCardsOrdered.sort(Player.HAND_ORDER_COMPARATOR);
        player.handOrder.addAll(newCardsOrdered);
        // order counts by hand order
        return player.handOrder.stream()
                .map(card -> {
                    Count count = new Count();
                    count.card = card.toString();
                    count.count = longCounts.get(card).intValue();
                    return count;
                }).collect(Collectors.toList());
    }

    private static int victoryPoints(Player player) {
        List<Card> deck = player.getDeck();
        int deckPoints = deck.stream()
                .filter(c -> c.isVictory() || c == Cards.CURSE)
                .map(c -> c.victoryValue(deck))
                .mapToInt(Integer::intValue)
                .sum();
        return deckPoints + player.getVictoryTokens();
    }

    private static class Differ<ViewType> {

        ViewType previous;
        ViewType current;
        JSONObject update;

        Differ(ViewType previous, ViewType current, JSONObject update) {
            this.previous = previous;
            this.current = current;
            this.update = update;
        }

        void diff(Function<ViewType, Object> getter, String tag) {
            diff(getter, tag, Function.identity());
        }

        <T> void diff(Function<ViewType, T> getter, String tag, Function<T, Object> toJson) {
            T currentValue = getter.apply(current);
            if (previous == null || !getter.apply(previous).equals(currentValue)) {
                update.put(tag, toJson.apply(currentValue));
            }
        }
    }

    JSONObject completeUpdate() {
        return computeUpdate(null);
    }

    JSONObject computeUpdate(GameView previousView) {
        JSONObject command = new JSONObject();
        command.put("command", "updateGameView");

        JSONObject updates = new JSONObject();

        Differ<GameView> differ = new Differ<>(previousView, this, updates);

        Map<String, JSONObject> pileUpdates = pileViews.keySet().stream()
                .map(pile -> new AbstractMap.SimpleEntry<>(pile, pileUpdate(previousView == null ? null : previousView.pileViews.get(pile), pileViews.get(pile))))
                .filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
        if (!pileUpdates.isEmpty()) {
            JSONObject jsonPileUpdates = new JSONObject();
            pileUpdates.forEach(jsonPileUpdates::put);
            updates.put("piles", jsonPileUpdates);
        }

        Map<String, Boolean> prizeCardUpdates = prizeCards.keySet().stream()
                .filter(prize -> previousView == null || previousView.prizeCards.get(prize) != prizeCards.get(prize))
                .collect(Collectors.toMap(Function.identity(), prizeCards::get));
        if (!prizeCardUpdates.isEmpty()) {
            updates.put("prizeCards", prizeCardUpdates);
        }

        Function<List<Count>, Object> countsToHtmlList = counts -> {
            if (counts.isEmpty()) {
                return "(empty)";
            }
            Map<Card, Integer> map = new HashMap<>();
            counts.forEach(c -> map.put(Cards.fromName(c.card), c.count));
            return Card.htmlList(map);
        };

        differ.diff(v -> v.trash, "trash", countsToHtmlList);
        differ.diff(v -> v.tradeRoute, "tradeRoute");

        JSONObject opponentUpdates = new JSONObject();
        for (int i = 0; i < opponentViews.size(); i++) {
            JSONObject opponentUpdate = opponentUpdate(previousView == null ? null : previousView.opponentViews.get(i), opponentViews.get(i));
            if (!opponentUpdate.isEmpty()) {
                opponentUpdates.put(i + "", opponentUpdate);
            }
        }
        if (!opponentUpdates.isEmpty()) {
            updates.put("opponents", opponentUpdates);
        }

        differ.diff(v -> v.drawSize, "drawSize");
        differ.diff(v -> v.discardSize, "discardSize");
        differ.diff(v -> v.actions, "actions");
        differ.diff(v -> v.buys, "buys");
        differ.diff(v -> v.coins, "coins");
        differ.diff(v -> v.isAutoplayingTreasures, "isAutoplayingTreasures");
        differ.diff(v -> v.coinTokens, "coinTokens");
        differ.diff(v -> v.pirateShip, "pirateShip");
        differ.diff(v -> v.victoryTokens, "victoryTokens");
        differ.diff(v -> v.victoryPoints, "victoryPoints");
        differ.diff(v -> v.nativeVillage, "nativeVillage", GameView::toJson);
        differ.diff(v -> v.island, "island", GameView::toJson);
        differ.diff(v -> v.durations, "durations", GameView::toJson);
        differ.diff(v -> v.inPlay, "inPlay", GameView::toJson);
        differ.diff(v -> v.hand, "hand", GameView::toJson);

        command.put("updates", updates);

        return command;
    }

    private static JSONObject pileUpdate(PileView previous, PileView current) {
        JSONObject pileUpdate = new JSONObject();

        Differ<PileView> differ = new Differ<>(previous, current, pileUpdate);

        differ.diff(v -> v.topCard, "topCard");
        differ.diff(v -> v.cost, "cost");
        differ.diff(v -> v.size, "size");
        differ.diff(v -> v.embargoTokens, "embargoTokens");
        differ.diff(v -> v.hasTradeRouteToken, "hasTradeRouteToken");

        return pileUpdate;
    }

    private static JSONObject opponentUpdate(OpponentView previous, OpponentView current) {
        JSONObject opponentUpdate = new JSONObject();

        Differ<OpponentView> differ = new Differ<>(previous, current, opponentUpdate);

        differ.diff(v -> v.username, "username");
        differ.diff(v -> v.handSize, "handSize");
        differ.diff(v -> v.drawSize, "drawSize");
        differ.diff(v -> v.discardSize, "discardSize");
        differ.diff(v -> v.victoryPoints, "victoryPoints");
        differ.diff(v -> v.durations, "durations", GameView::toJson);
        differ.diff(v -> v.inPlay, "inPlay", GameView::toJson);

        return opponentUpdate;
    }

    private static JSONArray toJson(List<Count> counts) {
        JSONArray json = new JSONArray();
        counts.forEach(count -> {
            JSONObject jsonCount = new JSONObject();
            jsonCount.put("card", count.card);
            jsonCount.put("count", count.count);
            json.add(jsonCount);
        });
        return json;
    }

}
