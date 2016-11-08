package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class Plaza extends Card {

    public Plaza() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
        // you may discard a treasure card for a coin token
        Set<Card> discardable = player.getHand().stream()
                .filter(c -> c.isTreasure)
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
            Card toDiscard = ((Bot) player).plazaDiscard(discardable);
            if (toDiscard != null && !discardable.contains(toDiscard)) {
                throw new IllegalStateException();
            }
            return toDiscard;
        }
        return game.sendPromptChooseFromHand(player, discardable,
                this.toString() + ": You may discard a treasure for a coin token.",
                "actionPrompt",
                false, "Discard nothing");
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+2 Actions", "You may discard a Treasure card.", "If you do, take a Coin token."};
    }

    @Override
    public String toString() {
        return "Plaza";
    }

}
