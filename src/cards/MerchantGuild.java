package cards;

import server.Card;
import server.Game;
import server.Player;

public class MerchantGuild extends Card {

    public MerchantGuild() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        plusCoins(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Buy", "+$1", "While this is in play, when you buy a card, take a Coin token."};
    }

    @Override
    public String toString() {
        return "Merchant Guild";
    }

}
