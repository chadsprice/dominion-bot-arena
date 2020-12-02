package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Merchant extends Card {

    @Override
    public String name() {
        return "Merchant";
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
                "The first time you_play a_[Silver] this_turn, <+1$>."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
    }

}
