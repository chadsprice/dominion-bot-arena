package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sentry extends Card {

    public Sentry() {
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
            if (choice < 0 || choice > 2) {
                throw new IllegalStateException();
            }
            return choice;
        }
        return game.promptMultipleChoice(player, this.toString() + ": You draw " + Card.htmlList(top) + ". What will you do with the " + card.htmlNameRaw() + "?", new String[] {"Trash", "Discard", "Put back"});
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Look at the top 2 cards of your deck. Trash and/or discard any number of them. Put the rest back on top in any order."};
    }

    @Override
    public String toString() {
        return "Sentry";
    }

    @Override
    public String plural() {
        return "Sentries";
    }

}
