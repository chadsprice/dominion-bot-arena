package cards;

import server.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Sentry extends Card {

    @Override
    public String name() {
        return "Sentry";
    }

    @Override
    public String plural() {
        return "Sentries";
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
                "Look at the_top_2 cards of your_deck. Trash and/or discard any_number of_them. Put the_rest back on_top in_any_order."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // look at the top 2 cards of your deck
        List<Card> top = player.takeFromDraw(2);
        if (!top.isEmpty()) {
            game.message(player, "drawing " + Card.htmlList(top));
            game.messageOpponents(player, "drawing " + Card.numCards(top.size()));
            // trash and/or discard any number of them
            List<Card> toPutBack = new ArrayList<>();
            Collections.sort(top, Player.HAND_ORDER_COMPARATOR);
            for (Card card : top) {
                int choice = chooseTrashDiscardOrPutBack(player, game, top, card);
                if (choice == 0) {
                    game.messageAll("trashing " + card.htmlName());
                    game.trash(player, card);
                } else if (choice == 1) {
                    game.messageAll("discarding " + card.htmlName());
                    player.addToDiscard(card);
                } else { // choice == 2
                    toPutBack.add(card);
                }
            }
            // put the rest back on top
            if (!toPutBack.isEmpty()) {
                game.messageAll("putting the rest back on top");
                putOnDeckInAnyOrder(player, game, toPutBack, this.toString() + ": Put the rest back on top in any order");
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, player.username + "'s deck is empty");
        }
    }

    private int chooseTrashDiscardOrPutBack(Player player, Game game, List<Card> top, Card card) {
        if (player instanceof Bot) {
            int choice = ((Bot) player).sentryTrashDiscardOrPutBack(card);
            checkMultipleChoice(3, choice);
            return choice;
        }
        return new Prompt(player, game)
                .message(this.toString() + ": You draw " + Card.htmlList(top) + ". What will you do with the " + card.htmlNameRaw() + "?")
                .multipleChoices(new String[] {"Trash", "Discard", "Put back"})
                .responseMultipleChoiceIndex();
    }

}
