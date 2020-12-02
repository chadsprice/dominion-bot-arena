package cards;

import server.Game;
import server.Player;

import java.util.List;

public class DameSylvia extends Knight {

    @Override
    public String name() {
        return "Dame Sylvia";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "<+2$>");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusCoins(player, game, 2);
    }

}
