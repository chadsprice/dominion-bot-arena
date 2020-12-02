package cards;

import server.Game;
import server.Player;

import java.util.List;

public class SirDestry extends Knight {

    @Override
    public String name() {
        return "Sir Destry";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "<+2_Cards>");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 2);
    }

}
