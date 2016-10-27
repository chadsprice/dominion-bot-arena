package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Armory extends Card {

    public Armory() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> gainable = game.cardsCostingAtMost(4);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Armory: Choose a card to gain onto your deck.");
            game.message(player, "gaining " + toGain.htmlName() + " onto your deck");
            game.messageOpponents(player, "gaining " + toGain.htmlName() + " onto their deck");
            game.gainToTopOfDeck(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a card costing up to $4, putting it on top of your deck."};
    }

    @Override
    public String toString() {
        return "Armory";
    }

    @Override
    public String plural() {
        return "Armories";
    }

}
