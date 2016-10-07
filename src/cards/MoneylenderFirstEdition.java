package cards;

import server.Card;
import server.Game;
import server.Player;

public class MoneylenderFirstEdition extends Card {

    public MoneylenderFirstEdition() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // if you have a Copper in hand
        if (player.getHand().contains(Card.COPPER)) {
            game.messageAll("trashing " + Card.COPPER.htmlName() + " for +$3");
            // trash it
            player.removeFromHand(Card.COPPER);
            game.addToTrash(Card.COPPER);
            // +$3
            player.addCoins(3);
        } else {
            game.messageAll("trashing nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Trash a Copper from your hand.", "If you do, +$3."};
    }

    @Override
    public String toString() {
        return "Moneylender (1st ed.)";
    }

    @Override
    public String plural() {
        return "Moneylenders (1st ed.)";
    }

}
