package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
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
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Artisan: Choose a card to gain to your hand");
            game.message(player, "gaining " + toGain.htmlName() + ", putting it into your hand");
            game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it into their hand");
            game.gainToHand(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
        // put a card onto your deck
        if (!player.getHand().isEmpty()) {
            Card toPutOnDeck = game.promptChoosePutOnDeck(player, new HashSet<Card>(player.getHand()), "Artisan", "attackPrompt");
            game.message(player, "putting " + toPutOnDeck.htmlName() + " on your deck");
            game.messageOpponents(player, "putting " + toPutOnDeck.htmlName() + " on their deck");
            player.putFromHandOntoDraw(toPutOnDeck);
        } else {
            game.message(player, "putting nothing on your deck because your hand is empty");
            game.messageOpponents(player, "putting nothing on their deck because their hand is empty");
        }
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
