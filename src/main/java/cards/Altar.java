package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class Altar extends Card {

    @Override
    public String name() {
        return "Altar";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public String[] description() {
        return new String[] {"Trash a_card from your_hand.", "Gain a_card costing up_to_5$."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        // trash a card from your hand
        if (!player.getHand().isEmpty()) {
            Card toTrash = promptChooseTrashFromHand(
                player,
                game,
                new HashSet<>(player.getHand()),
                this.toString() + ": Choose a card to trash."
            );
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
        } else {
            game.messageAll("having no card in hand to trash");
        }
        // gain a card costing up to $5
        gainCardCostingUpTo(player, game, 5);
    }

}
