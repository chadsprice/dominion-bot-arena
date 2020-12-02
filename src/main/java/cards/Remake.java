package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Remake extends Card {

    @Override
    public String name() {
        return "Remake";
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
        return new String[] {"Do this twice: Trash_a_card from your_hand, then gain a_card costing exactly 1$_more than the_trashed_card."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        for (int i = 0; i < 2; i++) {
            onRemodelVariant(player, game, 1, true);
        }
    }

}
