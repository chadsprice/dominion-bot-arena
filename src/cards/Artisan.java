package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Artisan extends Card {

    public Artisan() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // gain a card costing up to $5, putting it into your hand
        Set<Card> gainable = game.cardsCostingAtMost(5);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain to your hand.");
            game.message(player, "gaining " + toGain.htmlName() + " to your hand");
            game.messageOpponents(player, "gaining " + toGain.htmlName() + " to their hand");
            game.gainToHand(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
        // put a card from your hand onto your deck
        putACardFromYourHandOntoYourDeck(player, game);
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a card to your hand costing up to $5.", "Put a card from your hand onto your deck."};
    }

    @Override
    public String toString() {
        return "Artisan";
    }

}
