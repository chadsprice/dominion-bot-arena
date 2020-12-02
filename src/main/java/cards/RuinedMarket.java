package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class RuinedMarket extends Card {

    @Override
    public String name() {
        return "Ruined Market";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.RUINS);
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {"<+1_Buy>"};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
    }

}
