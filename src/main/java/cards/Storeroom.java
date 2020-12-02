package cards;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

import java.util.List;
import java.util.Set;

public class Storeroom extends Card {

    @Override
    public String name() {
        return "Storeroom";
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
                "<+1_Buy>",
                "Discard any_number of_cards.",
                "<+1_Card> per card discarded.",
                "Discard any_number of_cards.",
                "<+1$> per card discarded the second_time."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        // discard any number of cards
        List<Card> discarded = promptDiscardNumber(player, game, player.getHand().size(), Prompt.Amount.UP_TO);
        if (!discarded.isEmpty()) {
            game.messageAll("discarding " + Card.htmlList(discarded));
            player.putFromHandIntoDiscard(discarded);
            // +1 card per card discarded
            plusCards(player, game, discarded.size());
        } else {
            game.messageAll("discarding nothing");
        }
        // discard any number of cards
        discarded = promptDiscardNumber(player, game, player.getHand().size(), Prompt.Amount.UP_TO);
        if (!discarded.isEmpty()) {
            game.messageAll("discarding " + Card.htmlList(discarded) + " for +$" + discarded.size());
            player.putFromHandIntoDiscard(discarded);
            // +$1 per card discarded
            player.coins += discarded.size();
        } else {
            game.messageAll("discarding nothing");
        }
    }

}
