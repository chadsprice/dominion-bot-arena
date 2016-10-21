package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Develop extends Card {

    public Develop() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        if (!player.getHand().isEmpty()) {
            // trash a card from your hand
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Develop: Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.addToTrash(toTrash);
            // gain a card costing $1 more and a card costing $1 less, in either order
            int cost = toTrash.cost(game);
            List<Card> gained = new ArrayList<Card>();
            for (int i = 0; i < 2; i++) {
                Set<Card> gainable = new HashSet<Card>();
                // cards costing $1 more
                if (gained.isEmpty() || gained.get(0).cost(game) != cost + 1) {
                    gainable.addAll(game.cardsCostingExactly(cost + 1));
                }
                // cards costing $1 less
                if (gained.isEmpty() || gained.get(0).cost(game) != cost - 1) {
                    gainable.addAll(game.cardsCostingExactly(cost - 1));
                }
                if (!gainable.isEmpty()) {
                    Card toGain = game.promptChooseGainFromSupply(player, gainable, "Develop: Choose a card costing $1 more to gain, and a card costing $1 less to gain, in either order. (The second one will be on top of your deck.)");
                    game.messageAll("gaining " + toGain.htmlName());
                    game.gainToTopOfDeck(player, toGain);
                    gained.add(toGain);
                } else {
                    game.messageAll("gaining nothing");
                    break;
                }
            }
        } else {
            game.messageAll("having nothing in hand to trash");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Trash a card from your hand.", "Gain a card costing exactly $1 more than it and a card costing exactly $1 less than it, in either order, putting them on top of your deck."};
    }

    @Override
    public String toString() {
        return "Develop";
    }

}
