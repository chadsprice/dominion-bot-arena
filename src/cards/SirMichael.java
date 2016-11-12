package cards;

import server.Game;
import server.Player;

import java.util.List;

public class SirMichael extends Knight {

    public SirMichael() {
        super();
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // each other player discards down to 3 cards in hand
        handSizeAttack(targets, game, 3);
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "Each other player discards down to 3 cards in hand.");
    }

    @Override
    public String toString() {
        return "Sir Michael";
    }

}
