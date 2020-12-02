package cards;

import server.Game;
import server.Player;

import java.util.List;

public class SirBailey extends Knight {

    @Override
    public String name() {
        return "Sir Bailey";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "<+1_Card>");
        description.add(1, "<+1_Action>");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
    }

}
