package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cartographer extends Card {

    public Cartographer() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // look at the top 4 cards of your deck
        List<Card> top = player.takeFromDraw(4);
        if (!top.isEmpty()) {
            // discard any number of them
            List<Card> toDiscard = chooseDiscardAnyNumber(player, game, new ArrayList<Card>(top));
            if (!toDiscard.isEmpty()) {
                game.messageAll("discarding " + Card.htmlList(toDiscard));
                for (Card card : toDiscard) {
                    top.remove(card);
                }
                player.addToDiscard(toDiscard);
            }
            if (!top.isEmpty()) {
                putOnDeckInAnyOrder(player, game, top, "Cartographer: Put the rest back on top in any order");
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private List<Card> chooseDiscardAnyNumber(Player player, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            List<Card> toDiscard = ((Bot) player).cartographerDiscardAnyNumber(new ArrayList<Card>(cards));
            // verify that the bot can discard all of those cards
            for (Card eachToDiscard : toDiscard) {
                if (cards.remove(eachToDiscard)) {
                    throw new IllegalStateException();
                }
            }
            return toDiscard;
        }
        List<Card> toDiscard = new ArrayList<Card>();
        Collections.sort(cards, Player.HAND_ORDER_COMPARATOR);
        while (!cards.isEmpty()) {
            Card card = sendPromptPlayerDiscardOne(player, game, cards);
            if (card != null) {
                cards.remove(card);
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
        int choice = game.promptMultipleChoice(player, "Cartographer: Choose any number to discard.", choices);
        if (choice == choices.length - 1) {
            return null;
        } else {
            return cards.get(choice);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Look at the top 4 cards of your deck. Discard any number of them. Put the rest back on top in any order."};
    }

    @Override
    public String toString() {
        return "Cartographer";
    }

}
