package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class CandlestickMaker extends Card {

    @Override
    public String name() {
        return "Candlestick Maker";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Action>",
                "<+1_Buy>",
                "Take a Coin_token."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        plusBuys(player, game, 1);
        plusCoinTokens(player, game, 1);
    }

}
