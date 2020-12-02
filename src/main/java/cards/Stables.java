package cards;

import server.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Stables extends Card {

    @Override
    public String name() {
        return "Stables";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "You may discard a_Treasure.",
                "If you do, <+3_Cards> and <+1_Action>."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> treasures = player.getHand().stream()
                .filter(Card::isTreasure)
                .collect(Collectors.toSet());
        if (!treasures.isEmpty()) {
            Card toDiscard = chooseDiscardTreasure(player, game, treasures);
            if (toDiscard != null) {
                game.messageAll("discarding " + toDiscard.htmlName());
                player.putFromHandIntoDiscard(toDiscard);
                plusCards(player, game, 3);
                plusActions(player, game, 1);
            } else {
                game.messageAll("discarding nothing");
            }
        } else {
            game.messageAll("having no treasure to discard");
        }
    }

    private Card chooseDiscardTreasure(Player player, Game game, Set<Card> treasures) {
        if (player instanceof Bot) {
            Card toDiscard = ((Bot) player).stablesDiscard(Collections.unmodifiableSet(treasures));
            checkContains(treasures, toDiscard, false);
            return toDiscard;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may discard a treasure for +3 cards and +1 action.")
                .handChoices(treasures)
                .orNone("Discard nothing")
                .responseCard();
    }

}
