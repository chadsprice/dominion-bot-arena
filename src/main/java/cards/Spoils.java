package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Spoils extends Card {

    @Override
    public String name() {
        return "Spoils";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "3$",
                "When you play_this, return_it to the_[Spoils]_pile.",
                "(This_is_not in_the_Supply.)"
        };
    }

    @Override
    public int treasureValue() {
        return 3;
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        boolean returnedToPile = false;
        if (!hasMoved) {
            game.messageAll("returning the " + this.htmlNameRaw());
            player.removeFromPlay(this);
            game.returnToNonSupply(this);
            returnedToPile = true;
        }
        return returnedToPile;
    }

}
