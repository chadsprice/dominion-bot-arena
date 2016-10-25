package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class Remake extends Card {

    public Remake() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        for (int i = 0; i < 2; i++) {
            if (player.getHand().isEmpty()) {
                game.message(player, "your hand is empty");
                game.messageOpponents(player, "their hand is empty");
                break;
            }
            // trash a card from your hand
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Remake: Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.addToTrash(player, toTrash);
            // gain a card costing exactly $1 more
            Set<Card> gainable = game.cardsCostingExactly(toTrash.cost(game) + 1);
            if (!gainable.isEmpty()) {
                Card toGain = game.promptChooseGainFromSupply(player, gainable, "Remake: Choose a card to gain.");
                game.messageAll("gaining " + toGain.htmlName());
                game.gain(player, toGain);
            } else {
                game.messageAll("gaining nothing");
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Do this twice: Trash a card from your hand, then gain a card costing exactly $1 more than the trashed card."};
    }

    @Override
    public String toString() {
        return "Remake";
    }

}
