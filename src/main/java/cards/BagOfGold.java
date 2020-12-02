package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class BagOfGold extends Card {

    @Override
    public String name() {
        return "Bag of Gold";
    }

    @Override
    public String plural() {
        return "Bags of Gold";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public String htmlType() {
        return "Action-Prize";
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Action>",
                "Gain_a_[Gold], putting_it on_top_of your_deck.",
                "(This_is_not in_the_Supply.)"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        // gain a gold onto your deck
        gainOntoDeck(player, game, Cards.GOLD);
    }

}
