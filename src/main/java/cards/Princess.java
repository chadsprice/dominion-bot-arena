package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Princess extends Card {

    @Override
    public String name() {
        return "Princess";
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
                "<+1_Buy>",
                "While this is in_play, cards cost 2$_less, but not less_than_0$.",
                "(This_is_not in_the_Supply.)"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        game.messageAll("cards cost $2 less while this is in play");
    }

}
