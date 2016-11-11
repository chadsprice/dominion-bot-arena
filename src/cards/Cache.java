package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

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
    public void onGain(Player player, Game game) {
        // gain 2 Coppers
        gain(player, game, Cards.COPPER, 2);
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
