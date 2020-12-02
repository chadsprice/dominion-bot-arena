package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Scheme extends Card {

    @Override
    public String name() {
        return "Scheme";
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
                "<+1_Card>",
                "<+1_Action>",
                "At the start of Clean-up this_turn, you_may choose an_Action card you have in_play. If_you discard_it from_play this_turn, put_it on your_deck."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        game.schemesPlayedThisTurn++;
    }

}
