package cards;

import server.Card;
import server.Game;
import server.Player;

public class Madman extends Card {

    public Madman() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        plusActions(player, game, 2);
        boolean movedToNonSupply = false;
        // return this to the Madman pile
        if (!hasMoved) {
            game.messageAll("returning the " + this.htmlNameRaw());
            player.removeFromPlay(this);
            game.returnToNonSupply(this);
            movedToNonSupply = true;
            // +1 card per card in your hand
            if (!player.getHand().isEmpty()) {
                plusCards(player, game, player.getHand().size());
            }
        }
        return movedToNonSupply;
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Actions", "Return this to the Madman pile. If you do, +1 Card per card in your hand.", "(This is not in the Supply.)"};
    }

    @Override
    public String toString() {
        return "Madman";
    }

    @Override
    public String plural() {
        return "Madmen";
    }

}
