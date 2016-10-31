package cards;

import server.Game;
import server.Player;

import java.util.List;

public class DameMolly extends Knight {

    public DameMolly() {
        super();
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusActions(player, game, 2);
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "+2 Actions");
    }

    @Override
    public String toString() {
        return "Dame Molly";
    }

}
