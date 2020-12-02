package cards;

import server.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Cartographer extends Card {

    @Override
    public String name() {
        return "Cartographer";
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
                "<+1_Card>",
                "<+1_Action>",
                "Look_at the_top 4_cards of your_deck. Discard any_number of_them. Put the_rest back_on_top in_any_order."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // look at the top 4 cards of your deck
        List<Card> top = player.takeFromDraw(4);
        if (!top.isEmpty()) {
            game.message(player, "drawing " + Card.htmlList(top));
            game.messageOpponents(player, "drawing " + Card.numCards(top.size()));
            // discard any number of them
            List<Card> toDiscard = chooseDiscardAnyNumber(player, game, top);
            if (!toDiscard.isEmpty()) {
                game.messageAll("discarding " + Card.htmlList(toDiscard));
                toDiscard.forEach(top::remove);
                player.addToDiscard(toDiscard);
            }
            if (!top.isEmpty()) {
                game.messageAll("putting the rest back on top");
                putOnDeckInAnyOrder(
                        player,
                        game,
                        top,
                        this.toString() + ": Put the rest back on top in any order"
                );
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private List<Card> chooseDiscardAnyNumber(Player player, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            List<Card> toDiscard = ((Bot) player).cartographerDiscardAnyNumber(Collections.unmodifiableList(cards));
            checkContains(cards, toDiscard);
            return toDiscard;
        }
        List<Card> cardsCopy = new ArrayList<>(cards);
        List<Card> toDiscard = new ArrayList<>();
        Collections.sort(cardsCopy, Player.HAND_ORDER_COMPARATOR);
        while (!cardsCopy.isEmpty()) {
            Card card = sendPromptPlayerDiscardOne(player, game, cardsCopy);
            if (card != null) {
                cardsCopy.remove(card);
                toDiscard.add(card);
            } else {
                break;
            }
        }
        return toDiscard;
    }

    private Card sendPromptPlayerDiscardOne(Player player, Game game, List<Card> cards) {
        String[] choices = new String[cards.size() + 1];
        for (int i = 0; i < cards.size(); i++) {
            choices[i] = cards.get(i).toString();
        }
        choices[choices.length - 1] = "Done Discarding";
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Choose any number to discard")
                .multipleChoices(choices)
                .responseMultipleChoiceIndex();
        if (choice == choices.length - 1) {
            return null;
        } else {
            return cards.get(choice);
        }
    }

}
