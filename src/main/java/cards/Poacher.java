package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Poacher extends Card {

    @Override
    public String name() {
        return "Poacher";
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
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "<+1$>",
                "Discard a card per empty Supply_pile."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusCoins(player, game, 1);
        // discard a card per empty supply pile
        discardNumber(player, game, game.numEmptySupplyPiles());
    }

}
