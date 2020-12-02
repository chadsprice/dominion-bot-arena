package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Armory extends Card {

    @Override
    public String name() {
        return "Armory";
    }

    @Override
    public String plural() {
        return "Armories";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a_card costing up_to_4$, putting_it on_top of your_deck."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> gainable = game.cardsCostingAtMost(4);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                player,
                game,
                gainable,
                this.toString() + ": Choose a card to gain onto your deck."
            );
            game.message(player, "gaining " + toGain.htmlName() + " onto your deck");
            game.messageOpponents(player, "gaining " + toGain.htmlName() + " onto their deck");
            game.gainToTopOfDeck(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
    }

}
