package cards;

import server.Game;
import server.Player;

import java.util.List;

public class SirMichael extends Knight {

    @Override
    public String name() {
        return "Sir Michael";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "Each other_player discards down_to 3_cards in_hand.");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // each other player discards down to 3 cards in hand
        handSizeAttack(targets, game, 3);
    }

}
