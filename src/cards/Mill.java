package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Mill extends Card {

    public Mill() {
        isAction = true;
        isVictory = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public int victoryValue() {
        return 1;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // you may discard 2 cards for $2
        List<Card> toDiscard = game.promptDiscardNumber(player, 2, false, this.toString());
        if (toDiscard.size() == 2) {
            game.messageAll("discarding " + Card.htmlList(toDiscard) + " for +$2");
            player.putFromHandIntoDiscard(toDiscard);
            // benefit for discarding 2 cards
            player.addCoins(2);
        } else if (player.getHand().size() == 1 && toDiscard.size() == 1) {
            // if you have only one card in hand, you are allowed to discard it
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        } else {
            if (toDiscard.size() == 1) {
                // you tried to discard just one card when you weren't allowed to
                player.sendHand();
            }
            game.messageAll("discarding nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "You may discard 2 cards, for +$2.", "1 VP"};
    }

    @Override
    public String toString() {
        return "Mill";
    }

}
