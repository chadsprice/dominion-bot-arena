package cards;

import server.Card;
import server.Game;
import server.Player;

public class Fortress extends Card {

    public Fortress() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
    }

    @Override
    public boolean onTrashIsTrashed(Player player, Game game) {
        game.message(player, "putting the " + this.htmlNameRaw() + " into your hand");
        game.messageOpponents(player, "putting the " + this.htmlNameRaw() + " into their hand");
        player.addToHand(isBandOfMisfits ? Card.BAND_OF_MISFITS : this);
        return false;
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+2 Actions", "When you trash this, put it into your hand."};
    }

    @Override
    public String toString() {
        return "Fortress";
    }

    @Override
    public String plural() {
        return "Fortresses";
    }

}
