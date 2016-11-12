package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;

public class Altar extends Card {

    public Altar() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // trash a card from your hand
        if (!player.getHand().isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()), "Altar: Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
        } else {
            game.messageAll("revealing an empty hand, trashing nothing");
        }
        // gain a card costing up to $5
        gainCardCostingUpTo(player, game, 5);
    }

    @Override
    public String[] description() {
        return new String[] {"Trash a card from your hand.", "Gain a card costing up to $5."};
    }

    @Override
    public String toString() {
        return "Altar";
    }

}
