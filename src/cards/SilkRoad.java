package cards;

import server.Card;

import java.util.List;

public class SilkRoad extends Card {

    public SilkRoad() {
        isVictory = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public int victoryValue(List<Card> deck) {
        return (int) deck.stream().filter(c -> c.isVictory).count() / 4;
    }

    @Override
    public String[] description() {
        return new String[] {"Worth 1 VP for every 4 Victory cards in your deck (round down)."};
    }

    @Override
    public String toString() {
        return "Silk Road";
    }

}
