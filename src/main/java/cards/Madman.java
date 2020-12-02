package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Madman extends Card {

    @Override
    public String name() {
        return "Madman";
    }

    @Override
    public String plural() {
        return "Madmen";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Actions>",
                "Return this to the_[Madman]_pile. If_you_do, <+1_Card> per card in your_hand.",
                "(This_is_not in the_Supply.)"
        };
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        plusActions(player, game, 2);
        boolean returnedToPile = false;
        // return this to the Madman pile
        if (!hasMoved) {
            game.messageAll("returning the " + this.htmlNameRaw());
            player.removeFromPlay(this);
            game.returnToNonSupply(this);
            returnedToPile = true;
            // +1 card per card in your hand
            if (!player.getHand().isEmpty()) {
                plusCards(player, game, player.getHand().size());
            }
        }
        return returnedToPile;
    }

}
