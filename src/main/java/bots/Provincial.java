package bots;

import server.Bot;
import server.Card;
import server.Cards;

import java.util.*;

public class Provincial extends Bot {

    private static class CardCount {
        public Card card;
        public int count;

        public CardCount(Card card, int count) {
            this.card = card;
            this.count = count;
        }

        public CardCount copy() {
            return new CardCount(card, count);
        }
    }

    private List<CardCount> buyStrategy;
    private List<CardCount> currentCount;

    public Provincial() {

    }

    public static void main(String[] args) {
        Set<Card> kingdomCards = new HashSet<>(Arrays.asList(Cards.CELLAR, Cards.MARKET, Cards.MILITIA, Cards.MINE, Cards.MOAT, Cards.REMODEL, Cards.SMITHY, Cards.VILLAGE, Cards.WOODCUTTER, Cards.WORKSHOP));
        List<CardCount> buyStrategy = randomSeedStrategy(kingdomCards);
        System.out.println(buyStrategyToString(buyStrategy));
    }

    private static List<CardCount> randomSeedStrategy(Set<Card> kingdomCards) {
        List<CardCount> buyStrategy = new ArrayList<>();
        buyStrategy.add(randomCardCount(kingdomCards));
        buyStrategy.add(new CardCount(Cards.GOLD, -1));
        for (int i = 0; i < 3; i++) {
            buyStrategy.add(randomCardCount(kingdomCards));
        }
        buyStrategy.add(new CardCount(Cards.SILVER, -1));
        buyStrategy.add(randomCardCount(kingdomCards));
        return buyStrategy;
    }

    private static CardCount randomCardCount(Set<Card> cards) {
        Card card = randomCard(cards);
        int count = (int) (Math.random() * 10) + 1;
        return new CardCount(card, count);
    }

    private static Card randomCard(Set<Card> cards) {
        List<Card> list = new ArrayList<>(cards);
        return list.get((int) (Math.random() * list.size()));
    }

    private static String buyStrategyToString(List<CardCount> buyStrategy) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < buyStrategy.size(); i++) {
            str.append("(" + buyStrategy.get(i).card.toString() + "," + buyStrategy.get(i).count + ")");
            if (i != buyStrategy.size() - 1) {
                str.append(", ");
            }
        }
        return str.toString();
    }

}
