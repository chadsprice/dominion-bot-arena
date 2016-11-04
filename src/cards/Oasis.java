package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Oasis extends Card {

    public Oasis() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusCoins(player, game, 1);
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = game.promptDiscardNumber(player, 1, "Oasis");
            Card card = toDiscard.get(0);
            game.messageAll("discarding " + card.htmlName());
            player.putFromHandIntoDiscard(card);
        } else {
            game.message(player, "your hand is empty");
            game.messageOpponents(player, "their hand is empty");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "+$1", "Discard a card."};
    }

    @Override
    public String toString() {
        return "Oasis";
    }

    @Override
    public String plural() {
        return "Oases";
    }

}
