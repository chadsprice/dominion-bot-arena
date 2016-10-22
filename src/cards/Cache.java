package cards;

import server.Card;

public class Cache extends Card {

    public Cache() {
        isTreasure = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public int treasureValue() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {"$3", "When you gain this, gain 2 Coppers."};
    }

    @Override
    public String toString() {
        return "Cache";
    }

}
