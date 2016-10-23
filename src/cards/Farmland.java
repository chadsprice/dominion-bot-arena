package cards;

import server.Card;

public class Farmland extends Card {

    public Farmland() {
        isVictory = true;
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public int victoryValue() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {"2 VP", "When you buy this, trash a card from your hand.", "Gain a card costing exactly $2 more than the trashed card."};
    }

    @Override
    public String toString() {
        return "Farmland";
    }

}
