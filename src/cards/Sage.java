package cards;

import server.Card;
import server.Game;
import server.Player;

public class Sage extends Card {

    public Sage() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        revealUntil(player, game,
                c -> c.cost(game) >= 3,
                c -> putRevealedIntoHand(player, game, c));
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "Reveal cards from the top of your deck until you reveal one costing $3 or more. Put that card into your hand and discard the rest."};
    }

    @Override
    public String toString() {
        return "Sage";
    }

}
