package cards;

import server.Game;
import server.Player;

import java.util.List;

public class DameMolly extends Knight {

    @Override
    public String name() {
        return "Dame Molly";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "<+2_Actions>");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusActions(player, game, 2);
    }

}
