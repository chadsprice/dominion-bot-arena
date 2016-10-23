package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;

public class Mandarin extends Card {

    public Mandarin() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 3);
        if (!player.getHand().isEmpty()) {
            Card toPutOnDeck = game.promptChoosePutOnDeck(player, new HashSet<Card>(player.getHand()), "Mandarin: Put a card from your hand on top of your deck.");
            game.message(player, "putting " + toPutOnDeck.htmlName() + " on top of your deck");
            game.messageOpponents(player, "putting a card on top of their deck");
            player.putFromHandOntoDraw(toPutOnDeck);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+$3", "Put a card from your hand on top of your deck.", "When you gain this, put all Treasures you have in play on top of your deck in any order."};
    }

    @Override
    public String toString() {
        return "Mandarin";
    }

}
