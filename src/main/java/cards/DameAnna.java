package cards;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

import java.util.List;

public class DameAnna extends Knight {

    @Override
    public String name() {
        return "Dame Anna";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "You may trash up_to 2_cards from your_hand.");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // you may trash up to 2 cards from your hand
        if (!player.getHand().isEmpty()) {
            List<Card> toTrash = promptTrashNumber(
                    player,
                    game,
                    2,
                    Prompt.Amount.UP_TO
            );
            if (!toTrash.isEmpty()) {
                game.messageAll("trashing " + Card.htmlList(toTrash));
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
            }
        }
    }

}
