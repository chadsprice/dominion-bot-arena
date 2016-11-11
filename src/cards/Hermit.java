package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Hermit extends Card {

    public Hermit() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // you may trash a card from your discard or hand that is not a treasure
        Set<Card> trashableFromDiscard =
                player.getDiscard().stream()
                .filter(c -> !c.isTreasure)
                .collect(Collectors.toSet());
        Set<Card> trashableFromHand =
                player.getHand().stream()
                .filter(c -> !c.isTreasure)
                .collect(Collectors.toSet());
        if (!trashableFromDiscard.isEmpty() || !trashableFromHand.isEmpty()) {
            CardFromDiscardOrHand toTrash = chooseTrash(player, game, trashableFromDiscard, trashableFromHand);
            if (toTrash.card != null) {
                if (toTrash.isFromDiscard) {
                    game.message(player, "trashing " + toTrash.card.htmlName() + " from your discard");
                    game.messageOpponents(player, "trashing " + toTrash.card.htmlName() + " from their discard");
                    player.removeFromDiscard(toTrash.card, 1);
                    game.trash(player, toTrash.card);
                } else {
                    game.message(player, "trashing " + toTrash.card.htmlName() + " from your hand");
                    game.messageOpponents(player, "trashing " + toTrash.card.htmlName() + " from their hand");
                    player.removeFromHand(toTrash.card);
                    game.trash(player, toTrash.card);
                }
            } else {
                game.messageAll("trashing nothing");
            }
        } else {
            game.message(player, "having no non-treasures in your discard or hand to trash");
            game.messageOpponents(player, "having no non-treasures in their discard or hand to trash");
        }
        // gain a card costing up to $4
        Set<Card> gainable = game.cardsCostingAtMost(3);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Hermit: Choose a card to gain.");
            game.messageAll("gaining " + toGain.htmlName());
            game.gain(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
    }

    private CardFromDiscardOrHand chooseTrash(Player player, Game game, Set<Card> trashableFromDiscard, Set<Card> trashableFromHand) {
        if (player instanceof Bot) {
            CardFromDiscardOrHand toTrash = ((Bot) player).hermitTrash(trashableFromDiscard, trashableFromHand);
            if ((toTrash.isFromDiscard && !trashableFromDiscard.contains(toTrash.card)) ||
                    (!toTrash.isFromDiscard && !trashableFromHand.contains(toTrash.card))) {
                throw new IllegalStateException();
            }
            return toTrash;
        }
        List<Card> trashableFromDiscardSorted = new ArrayList<>(trashableFromDiscard);
        Collections.sort(trashableFromDiscardSorted, Player.HAND_ORDER_COMPARATOR);
        String[] choices = new String[trashableFromDiscardSorted.size() + 1];
        for (int i = 0; i < trashableFromDiscardSorted.size(); i++) {
            choices[i] = trashableFromDiscardSorted.get(i).toString();
        }
        choices[choices.length - 1] = "Trash nothing";
        Object choice = game.sendPromptChooseFromHandOrMultipleChoice(player, trashableFromHand, this.toString() + ": You may trash a card from your discard pile or hand that is not a treasure. Choose one from your hand to trash that one from your hand, choose one below to trash it from your discard, or choose to trash nothing.", "attackPrompt", choices);
        if (choice instanceof Integer) {
            int choiceIndex = (Integer) choice;
            if (choiceIndex == choices.length - 1) {
                return new CardFromDiscardOrHand(null, false);
            } else {
                return new CardFromDiscardOrHand(trashableFromDiscardSorted.get(choiceIndex), true);
            }
        } else { // choice instanceof Card
            return new CardFromDiscardOrHand(((Card) choice), false);
        }
    }

    public static class CardFromDiscardOrHand {
        Card card;
        boolean isFromDiscard;

        public CardFromDiscardOrHand(Card card, boolean isFromDiscard) {
            this.card = card;
            this.isFromDiscard = isFromDiscard;
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Look through your discard pile.", "You may trash a card from your discard pile or hand that is not a Treasure.", "Gain a card costing up to $3.", "When you discard this from play, if you did not buy any cards this turn, trash this and gain a Madman from the Madman pile."};
    }

    @Override
    public String toString() {
        return "Hermit";
    }

}
