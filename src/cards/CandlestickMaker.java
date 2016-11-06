package cards;

import server.Card;
import server.Game;
import server.Player;

public class CandlestickMaker extends Card {

    public CandlestickMaker() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        plusBuys(player, game, 1);
        plusCoinTokens(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "+1 Buy", "Take a Coin token."};
    }

    @Override
    public String toString() {
        return "Candlestick Maker";
    }

}
