package cards;

import server.Card;
import server.Game;
import server.Player;

public class Spoils extends Card {

    public Spoils() {
        isTreasure = true;
    }

    @Override
    public int cost() {
        return 0;
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

    @Override
    public String[] description() {
        return new String[] {"$3", "When you play this, return it to the Spoils pile.", "(This is not in the Supply.)"};
    }

    @Override
    public String toString() {
        return "Spoils";
    }

    @Override
    public String plural() {
        return toString();
    }

}
