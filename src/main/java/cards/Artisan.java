package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Artisan extends Card {

    @Override
    public String name() {
        return "Artisan";
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
        return new String[] {"Gain_a_card to_your_hand costing up_to_5$.", "Put_a_card from_your_hand onto_your_deck."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        // gain a card costing up to $5, putting it into your hand
        Set<Card> gainable = game.cardsCostingAtMost(5);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                player,
                game,
                gainable,
                this.toString() + ": Choose a card to gain to your hand."
            );
            game.message(player, "gaining " + toGain.htmlName() + " to your hand");
            game.messageOpponents(player, "gaining " + toGain.htmlName() + " to their hand");
            game.gainToHand(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
        // put a card from your hand onto your deck
        putACardFromYourHandOntoYourDeck(player, game);
    }
}
