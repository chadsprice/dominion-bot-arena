package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class MerchantGuild extends Card {

    @Override
    public String name() {
        return "Merchant Guild";
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
                "<+1_Buy>",
                "<+1$>",
                "While this is in_play, when_you buy a_card, take a_Coin_token."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        plusCoins(player, game, 1);
    }

}
