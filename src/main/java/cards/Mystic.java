package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Mystic extends Card {

    @Override
    public String name() {
        return "Mystic";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Action>",
                "<+2$>",
                "Name a card.",
                "Reveal the top card of your_deck.", "If it's the named_card, put_it into_your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        plusCoins(player, game, 2);
        tryToNameTopCardOfDeck(player, game);
    }

}
