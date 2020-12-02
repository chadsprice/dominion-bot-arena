package cards;

import server.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Plaza extends Card {

    @Override
    public String name() {
        return "Plaza";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+2_Actions>",
                "You may discard a Treasure card.",
                "If you do, take a Coin token."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
        // you may discard a treasure card for a coin token
        Set<Card> discardable = player.getHand().stream()
                .filter(Card::isTreasure)
                .collect(Collectors.toSet());
        if (!discardable.isEmpty()) {
            Card toDiscard = chooseDiscard(player, game, discardable);
            if (toDiscard != null) {
                game.messageAll("discarding " + toDiscard.htmlName() + " for a coin token");
                player.putFromHandIntoDiscard(toDiscard);
                player.addCoinTokens(1);
            }
        }
    }

    private Card chooseDiscard(Player player, Game game, Set<Card> discardable) {
        if (player instanceof Bot) {
            Card toDiscard = ((Bot) player).plazaDiscard(Collections.unmodifiableSet(discardable));
            checkContains(discardable, toDiscard, false);
            return toDiscard;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may discard a treasure for a coin token.")
                .handChoices(discardable)
                .orNone("Discard nothing")
                .responseCard();
    }

}
