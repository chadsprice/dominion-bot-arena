package cards;

import server.*;

import java.util.*;

public class Harbinger extends Card {

    @Override
    public String name() {
        return "Harbinger";
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
                "<+1_Card>",
                "<+1_Action>",
                "Look through your discard_pile. You_may put a_card from_it onto your_deck."
        };
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
            Card toHarbinger = ((Bot) player).harbingerPutFromDiscardOntoDeck(Collections.unmodifiableSet(cards));
            checkContains(cards, toHarbinger, false);
            return toHarbinger;
        }
        return promptMultipleChoiceCard(
                player,
                game,
                Prompt.Type.NORMAL,
                this.toString() + ": You may put a card from your discard onto your deck.",
                cards,
                "None"
        );
    }

}
