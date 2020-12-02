package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class RuinedVillage extends Card {

    @Override
    public String name() {
        return "Ruined Village";
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
        return new String[] {"<+1_Action>"};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
    }

}
