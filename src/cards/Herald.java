package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Herald extends Card {

    public Herald() {
        isAction = true;
        isOverpayable = true;
    }

    @Override
    public int cost() {
        return 4;
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
            if (card.isAction) {
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
            Card toPutOnDeck = ((Bot) player).heraldPutFromDiscardOntoDeck(choices);
            if (!choices.contains(toPutOnDeck)) {
                throw new IllegalStateException();
            }
            return toPutOnDeck;
        }
        int totalRemaining = Math.min(number, player.getDiscard().size());
        return game.promptMultipleChoiceCard(player,
                this.toString() + ": Your discard pile is " + Card.htmlList(player.getDiscard()) + ". Put " + Card.numCards(totalRemaining) + " from your discard on top of your deck (the last card you choose will be on top of your deck)",
                "actionPrompt", choices);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Reveal the top card of your deck.", "If it is an Action, play it.", "When you buy this, you may overpay for it. For each $1 you overpaid, look through your discard pile and put a card from it on top of your deck."};
    }

    @Override
    public String toString() {
        return "Herald";
    }

}
