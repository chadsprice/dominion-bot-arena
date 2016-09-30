package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Map;

public class Poacher extends Card {

    public Poacher() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusCoins(player, game, 1);
        // discard a card per empty supply pile
        int numEmpty = numEmptySupplyPiles(game);
        if (numEmpty != 0 && !player.getHand().isEmpty()) {
            List<Card> toDiscard = game.promptDiscardNumber(player, numEmpty, "Poacher", "attackPrompt");
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
    }

    private int numEmptySupplyPiles(Game game) {
        int numEmpty = 0;
        for (Map.Entry<Card, Integer> entry : game.supply.entrySet()) {
            if (entry.getValue() == 0) {
                numEmpty++;
            }
        }
        return numEmpty;
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "+$1", "Discard a card per empty Supply pile."};
    }

    @Override
    public String toString() {
        return "Poacher";
    }

}
