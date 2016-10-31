package cards;

import server.Game;
import server.Player;

import java.util.List;

public class SirDestry extends Knight {

    public SirDestry() {
        super();
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 2);
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "+2 Cards");
    }

    @Override
    public String toString() {
        return "Sir Destry";
    }

}
