package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Menagerie extends Card {

    @Override
    public String name() {
        return "Menagerie";
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
                "<+1_Action>",
                "Reveal your hand.",
                "If there are no duplicate cards in_it,",
                "* <+3_Cards>",
                "Otherwise,",
                "* <+1_Card>"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        // reveal hand
        boolean hasDuplicates = containsDuplicates(player.getHand());
        if (!player.getHand().isEmpty()) {
            String duplicatesStr = hasDuplicates ? ", which has duplicates" : ", which has no duplicates";
            game.messageAll("revealing " + Card.htmlList(player.getHand()) + duplicatesStr);
        } else {
            game.messageAll("revealing an empty hand, which has no duplicates");
        }
        if (!hasDuplicates) {
            // no duplicates in hand -> +3 cards
            plusCards(player, game, 3);
        } else {
            // duplicates
            plusCards(player, game, 1);
        }
    }

    private boolean containsDuplicates(Collection<Card> cards) {
        // if there are duplicates, the Collection will be larger than the Set
        return cards.size() > new HashSet<>(cards).size();
    }

}
