package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class MoneylenderFirstEdition extends Card {

    @Override
    public String name() {
        return "Moneylender (1st ed.)";
    }

    @Override
    public String plural() {
        return "Moneylenders (1st ed.)";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Trash a_[Copper] from your_hand.",
                "If_you_do, <+3$>."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        // if you have a Copper in hand
        if (player.getHand().contains(Cards.COPPER)) {
            game.messageAll("trashing " + Cards.COPPER.htmlName() + " for +$3");
            // trash it
            player.removeFromHand(Cards.COPPER);
            game.trash(player, Cards.COPPER);
            // +$3
            player.coins += 3;
        } else {
            game.messageAll("trashing nothing");
        }
    }

}
