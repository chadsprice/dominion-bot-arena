package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class AbandonedMine extends Card {

    @Override
    public String name() {
        return "Abandoned Mine";
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
        return new String[] {"<+1$>"};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 1);
    }

}
