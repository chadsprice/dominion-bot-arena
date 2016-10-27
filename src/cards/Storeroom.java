package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Storeroom extends Card {

    public Storeroom() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // discard any number of cards
        List<Card> discarded = game.promptDiscardNumber(player, player.getHand().size(), false, "Storeroom");
        if (!discarded.isEmpty()) {
            game.messageAll("discarding " + Card.htmlList(discarded));
            player.putFromHandIntoDiscard(discarded);
            // +1 card per card discarded
            plusCards(player, game, discarded.size());
        } else {
            game.messageAll("discarding nothing");
        }
        // discard any number of cards
        discarded = game.promptDiscardNumber(player, player.getHand().size(), false, "Storeroom");
        if (!discarded.isEmpty()) {
            game.messageAll("discarding " + Card.htmlList(discarded) + " for +$" + discarded.size());
            player.putFromHandIntoDiscard(discarded);
            // +$1 per card discarded
            player.addCoins(discarded.size());
        } else {
            game.messageAll("discarding nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Buy", "Discard any number of cards.", "+1 Card per card discarded.", "Discard any number of cards.", "+$1 per card discarded the second time."};
    }

    @Override
    public String toString() {
        return "Storeroom";
    }

}
