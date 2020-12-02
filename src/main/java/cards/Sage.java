package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Sage extends Card {

    @Override
    public String name() {
        return "Sage";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Action>",
                "Reveal cards from the_top of your_deck until you reveal one costing 3$_or_more. Put that_card into your_hand and discard_the_rest."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        revealUntil(
                player,
                game,
                c -> c.cost(game) >= 3,
                c -> putRevealedIntoHand(player, game, c)
        );
    }

}
