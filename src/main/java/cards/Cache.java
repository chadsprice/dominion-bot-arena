package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class Cache extends Card {

    @Override
    public String name() {
        return "Cache";
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "3$",
                "When_you gain_this, gain 2_[Coppers]."
        };
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

}
