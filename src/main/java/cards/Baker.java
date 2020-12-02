package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Baker extends Card {

    @Override
    public String name() {
        return "Baker";
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
                "<+1_Card>",
                "<+1_Action>",
                "Take a Coin_token.",
                "Setup: Each_player takes a_Coin_token."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusCoinTokens(player, game, 1);
    }

}
