package cards;

import server.Card;
import server.Game;
import server.Player;

public class Remake extends Card {

    public Remake() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        for (int i = 0; i < 2; i++) {
            onRemodelVariant(player, game, 1, true);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Do this twice: Trash a card from your hand, then gain a card costing exactly $1 more than the trashed card."};
    }

    @Override
    public String toString() {
        return "Remake";
    }

}
