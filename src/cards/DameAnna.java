package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class DameAnna extends Knight {

    public DameAnna() {
        super();
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // you may trash up to 2 cards from your hand
        if (!player.getHand().isEmpty()) {
            List<Card> toTrash = game.promptTrashNumber(player, 2, false, "Dame Anna");
            if (!toTrash.isEmpty()) {
                game.messageAll("trashing " + Card.htmlList(toTrash));
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
            }
        }
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "You may trash up to 2 cards from your hand.");
    }

    @Override
    public String toString() {
        return "Dame Anna";
    }

}
