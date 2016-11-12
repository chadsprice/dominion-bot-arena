package cards;

import server.Card;

import java.util.HashSet;
import java.util.List;

public class Fairgrounds extends Card {

    public Fairgrounds() {
        isVictory = true;
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public int victoryValue(List<Card> deck) {
        return 2 * (new HashSet<>(deck).size() / 5);
    }

    @Override
    public String[] description() {
        return new String[] {"Worth 2 VP for every 5 differently named cards in your deck (rounded down)."};
    }

    @Override
    public String toString() {
        return "Fairgrounds";
    }

    @Override
    public String plural() {
        return toString();
    }

}
