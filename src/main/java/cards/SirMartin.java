package cards;

import server.Game;
import server.Player;

import java.util.List;

public class SirMartin extends Knight {

    @Override
    public String name() {
        return "Sir Martin";
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "<+2_Buys>");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusBuys(player, game, 2);
    }

}
