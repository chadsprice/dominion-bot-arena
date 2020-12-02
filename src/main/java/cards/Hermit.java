package cards;

import server.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Hermit extends Card {

    @Override
    public String name() {
        return "Hermit";
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
                "Look through your discard_pile.",
                "You may trash a_card from your discard_pile or hand that is not a_Treasure.",
                "Gain a_card costing up_to_3$.", "When you discard_this from_play, if you did_not buy any_cards this_turn, trash_this and gain a_[Madman] from the_[Madman]_pile."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        // you may trash a card from your discard or hand that is not a treasure
        Set<Card> trashableFromDiscard =
                player.getDiscard().stream()
                .filter(c -> !c.isTreasure())
                .collect(Collectors.toSet());
        Set<Card> trashableFromHand =
                player.getHand().stream()
                .filter(c -> !c.isTreasure())
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
        // gain a card costing up to $3
        Set<Card> gainable = game.cardsCostingAtMost(3);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": Choose a card to gain."
            );
            game.messageAll("gaining " + toGain.htmlName());
            game.gain(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
    }

    private CardFromDiscardOrHand chooseTrash(Player player, Game game, Set<Card> trashableFromDiscard, Set<Card> trashableFromHand) {
        if (player instanceof Bot) {
            CardFromDiscardOrHand toTrash = ((Bot) player).hermitTrash(Collections.unmodifiableSet(trashableFromDiscard), Collections.unmodifiableSet(trashableFromHand));
            if (toTrash.isFromDiscard) {
                checkContains(trashableFromDiscard, toTrash.card, false);
            } else {
                checkContains(trashableFromHand, toTrash.card, false);
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
        PromptResponse response = new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may trash a card from your discard pile or hand that is not a treasure. The cards you may trash from your discard pile are:")
                .handChoices(trashableFromHand)
                .multipleChoices(choices)
                .response();
        if (response.handChoice != null) {
            return new CardFromDiscardOrHand(response.handChoice, false);
        } else if (response.multipleChoiceIndex == choices.length - 1) {
            return new CardFromDiscardOrHand(null, false);
        } else {
            return new CardFromDiscardOrHand(trashableFromDiscardSorted.get(response.multipleChoiceIndex), true);
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

}
