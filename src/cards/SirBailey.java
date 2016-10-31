package cards;

import server.Game;
import server.Player;

import java.util.List;

public class SirBailey extends Knight {

    public SirBailey() {
        super();
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "+1 Card");
        description.add(1, "+1 Action");
    }

    @Override
    public String toString() {
        return "Sir Bailey";
    }

}
