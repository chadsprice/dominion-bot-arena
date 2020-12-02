package cards;

import server.Card;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fairgrounds extends Card {

    @Override
    public String name() {
        return "Fairgrounds";
    }

    @Override
    public String plural() {
        return toString();
    }

    @Override
    public Set<Type> types() {
        return types(Type.VICTORY);
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public String[] description() {
        return new String[] {"Worth 2_VP for every_5 differently_named cards in your_deck (rounded_down)."};
    }

    @Override
    public int victoryValue(List<Card> deck) {
        return 2 * (new HashSet<>(deck).size() / 5);
    }

}
