package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Oasis extends Card {

    @Override
    public String name() {
        return "Oasis";
    }

    @Override
    public String plural() {
        return "Oases";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "<+1$>",
                "Discard a card."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusCoins(player, game, 1);
        if (!player.getHand().isEmpty()) {
            Card toDiscard = promptDiscardNumber(player, game, 1).get(0);
            game.messageAll("discarding " + toDiscard.htmlName());
            player.putFromHandIntoDiscard(toDiscard);
        } else {
            game.message(player, "your hand is empty");
            game.messageOpponents(player, "their hand is empty");
        }
    }

}
