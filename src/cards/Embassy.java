package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Embassy extends Card {

    public Embassy() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 5);
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = game.promptDiscardNumber(player, 3, "Embassy");
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
    }

    @Override
    public void onGain(Player player, Game game) {
        // each other player gains a Silver
        game.getOpponents(player).forEach(opponent -> {
            if (game.supply.get(Card.SILVER) != 0) {
                game.message(opponent, "You gain " + Card.SILVER.htmlName() + " because of " + Card.EMBASSY.htmlNameRaw());
                game.messageOpponents(opponent, opponent.username + " gains " + Card.SILVER.htmlName() + " because of " + Card.EMBASSY.htmlNameRaw());
                game.gain(opponent, Card.SILVER);
            }
        });
    }

    @Override
    public String[] description() {
        return new String[] {"+5 Cards", "Discard 3 cards.", "When you gain this, each other player gains a Silver."};
    }

    @Override
    public String toString() {
        return "Embassy";
    }

    @Override
    public String plural() {
        return "Embassies";
    }

}
