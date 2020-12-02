package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Necropolis extends Card {

    @Override
    public String name() {
        return "Necropolis";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.SHELTER);
    }

    @Override
    public String htmlType() {
        return "Action-Shelter";
    }

    @Override
    public String htmlHighlightType() {
        return "action-shelter";
    }

    @Override
    public int cost() {
        return 1;
    }

    @Override
    public String[] description() {
        return new String[] {"<+2_Actions>"};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 2);
    }

}
