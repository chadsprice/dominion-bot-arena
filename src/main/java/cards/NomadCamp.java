package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class NomadCamp extends Card {

    @Override
    public String name() {
        return "Nomad Camp";
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
                "<+1_Buy>",
                "<+2$>",
                "When you gain_this, put_it on_top of your_deck."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        plusCoins(player, game, 2);
    }

}
