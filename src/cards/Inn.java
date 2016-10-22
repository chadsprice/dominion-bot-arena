package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Inn extends Card {

    public Inn() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        plusActions(player, game, 2);
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = game.promptDiscardNumber(player, 2, "Inn", "actionPrompt");
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "+2 Actions", "Discard 2 cards.", "When you gain this, look through your discard pile (including this), reveal any number of Action cards from it, and shuffle them into your deck."};
    }

    @Override
    public String toString() {
        return "Inn";
    }

}
