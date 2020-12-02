package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Highway extends Card {

    @Override
    public String name() {
        return "Highway";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "While this is in_play, cards cost 1$_less, but not less_than_0$."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        game.messageAll("cards cost $1 less while this is in play");
    }

}
