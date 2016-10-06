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
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Artisan: Choose a card to gian to your hand");
            game.message(player, "gaining " + toGain.htmlName() + ", putting it into your hand");
            game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it into his hand");
            game.gainToHand(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
        // discard a card
        if (!player.getHand().isEmpty()) {
            Card toDiscard = game.promptDiscardNumber(player, 1, "Artisan", "attackPrompt").get(0);
            game.messageAll("discarding " + toDiscard.htmlName());
            player.putFromHandIntoDiscard(toDiscard);
        } else {
            game.message(player, "your hand is empty");
            game.messageOpponents(player, player.username + "'s hand is empty");
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
