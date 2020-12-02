package cards;

import server.Card;

import java.util.List;
import java.util.Set;

public class SilkRoad extends Card {

    @Override
    public String name() {
        return "Silk Road";
    }

    @Override
    public Set<Type> types() {
        return types(Type.VICTORY);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {"Worth 1_VP for every 4_Victory cards in your_deck (rounded_down)."};
    }

    @Override
    public int victoryValue(List<Card> deck) {
        return (int) deck.stream().filter(Card::isVictory).count() / 4;
    }

}
