package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Diadem extends Card {

    @Override
    public String name() {
        return "Diadem";
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public String htmlType() {
        return "Treasure-Prize";
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "2$",
                "When you play_this, <+1$>_per unused_Action you have (Action, not Action_card).",
                "(This_is not in the_Supply.)"
        };
    }

    @Override
    public int treasureValue() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // +$1 per unused action
        plusCoins(player, game, player.actions);
    }

}
