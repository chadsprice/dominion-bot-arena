package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;

public class Trader extends Card {

    public Trader() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        if (!player.getHand().isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Trader: Choose a card to trash, and gain a number of Silvers equal to its cost in coins.");
            int numSilvers = Math.min(toTrash.cost(game), game.supply.get(Card.SILVER));
            game.messageAll("trashing " + toTrash + " and gaining " + Card.SILVER.htmlName(numSilvers));
            player.removeFromHand(toTrash);
            game.addToTrash(player, toTrash);
            for (int i = 0; i < numSilvers; i++) {
                game.gain(player, Card.SILVER);
            }
        } else {
            game.messageAll("having no card in hand to trash");
        }
    }

    @Override
    public String htmlClass() {
        return "reaction";
    }

    @Override
    public String htmlType() {
        return "Action-Reaction";
    }

    @Override
    public String[] description() {
        return new String[] {"Trash a card from your hand.", "Gain a number of Silvers equal to its cost in coins.", "When you would gain a card, you may reveal this from your hand.", "If you do, instead, gain a Silver."};
    }

    @Override
    public String toString() {
        return "Trader";
    }

}
