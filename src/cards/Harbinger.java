package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.*;

public class Harbinger extends Card {

    public Harbinger() {
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
        // you may put a card from your discard onto your deck
        if (!player.getDiscard().isEmpty()) {
            Card toHarbinger = choosePutFromDiscardOntoDeck(player, game, new HashSet<>(player.getDiscard()));
            if (toHarbinger != null) {
                game.message(player, "putting " + toHarbinger.htmlName() + " from your discard onto of your deck");
                game.messageOpponents(player, "putting " + toHarbinger.htmlName() + " from their discard onto of their deck");
                player.removeFromDiscard(toHarbinger);
                player.putOnDraw(toHarbinger);
            }
        } else {
            game.message(player, "your discard is empty");
            game.messageOpponents(player, "their discard is empty");
        }
    }

    private Card choosePutFromDiscardOntoDeck(Player player, Game game, Set<Card> cards) {
        if (player instanceof Bot) {
            Card toHarbinger = ((Bot) player).harbingerPutFromDiscardOntoDeck(cards);
            if (toHarbinger != null && !cards.contains(toHarbinger)) {
                throw new IllegalStateException();
            }
            return toHarbinger;
        }
        return game.promptMultipleChoiceCard(player, "Harbinger: You may put a card from your discard onto your deck.", "actionPrompt", cards, "None");
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Look through your discard pile. You may put a card from it onto your deck."};
    }

    @Override
    public String toString() {
        return "Harbinger";
    }

}
