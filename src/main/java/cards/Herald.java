package cards;

import server.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Herald extends Card {

    @Override
    public String name() {
        return "Herald";
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
    public boolean isOverpayable() {
        return true;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "Reveal the_top_card of your_deck.", "If it_is an_Action, play_it.", "When you buy_this, you may overpay for_it. For each_1$ you_overpaid, look_through your discard_pile and put a_card from_it on_top of your_deck."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // reveal the top card of your deck
        List<Card> drawn = player.takeFromDraw(1);
        if (!drawn.isEmpty()) {
            Card card = drawn.get(0);
            // if it is an action, play it
            if (card.isAction()) {
                game.messageAll("revealing " + card.htmlName() + " and playing it");
                player.addToPlay(card);
                game.playAction(player, card, false);
            } else {
                game.messageAll("revealing " + card.htmlName() + " and putting it back");
                player.putOnDraw(card);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    @Override
    public void onOverpay(Player player, Game game, int amountOverpaid) {
        // for each $1 you overpaid, put a card from your discard on top of your deck
        for (int i = 0; i < amountOverpaid; i++) {
            if (player.getDiscard().isEmpty()) {
                game.message(player, "your discard is empty");
                game.messageOpponents(player, "their discard is empty");
                break;
            }
            Card toPutOnDeck = choosePutFromDiscardOntoDeck(player, game, (amountOverpaid - i));
            game.message(player, "putting " + toPutOnDeck.htmlName() + " from your discard on top of your deck");
            game.messageOpponents(player, "putting " + toPutOnDeck.htmlName() + " from their discard on top of their deck");
            player.removeFromDiscard(toPutOnDeck);
            player.putOnDraw(toPutOnDeck);
        }
    }

    private Card choosePutFromDiscardOntoDeck(Player player, Game game, int number) {
        Set<Card> choices = new HashSet<>(player.getDiscard());
        if (player instanceof Bot) {
            Card toPutOnDeck = ((Bot) player).heraldPutFromDiscardOntoDeck(Collections.unmodifiableSet(choices));
            checkContains(choices, toPutOnDeck);
            return toPutOnDeck;
        }
        int totalRemaining = Math.min(number, player.getDiscard().size());
        return promptMultipleChoiceCard(
                player,
                game,
                Prompt.Type.NORMAL,
                this.toString() + ": Your discard pile is " + Card.htmlList(player.getDiscard()) + ". Put " + Card.numCards(totalRemaining) + " from your discard on top of your deck (the last card you choose will be on top of your deck)",
                choices
        );
    }
}
