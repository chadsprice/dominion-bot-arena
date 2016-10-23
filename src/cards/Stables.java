package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class Stables extends Card {

    public Stables() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> treasures = player.getHand().stream().filter(c -> c.isTreasure).collect(Collectors.toSet());
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

    public Card chooseDiscardTreasure(Player player, Game game, Set<Card> treasures) {
        if (player instanceof Bot) {
            Card toDiscard = ((Bot) player).stablesDiscard(treasures);
            // check the Bot response
            if (toDiscard != null && !treasures.contains(toDiscard)) {
                throw new IllegalStateException();
            }
            return toDiscard;
        }
        return game.sendPromptChooseFromHand(player, treasures, "Stables: You may discard a treasure for +3 cards and +1 action.", "actionPrompt", false, "Discard nothing");
    }

    @Override
    public String[] description() {
        return new String[] {"You may discard a Treasure.", "If you do, +3 Cards and +1 Action."};
    }

    @Override
    public String toString() {
        return "Stables";
    }

    @Override
    public String plural() {
        return toString();
    }

}
