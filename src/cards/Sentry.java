package cards;

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
        List<Card> top = player.takeFromDraw(2);
        if (!top.isEmpty()) {
            Collections.sort(top, Player.HAND_ORDER_COMPARATOR);
            List<Card> toPutBack = new ArrayList<Card>();
            game.message(player, "drawing " + Card.htmlList(top));
            game.messageOpponents(player, "drawing " + Card.numCards(top.size()));
            for (Card card : top) {
                int choice = game.promptMultipleChoice(player, "Sentry: You draw " + Card.htmlList(top) + ". What will you do with the " + card.htmlNameRaw() + "?", new String[] {"Trash", "Discard", "Put back"});
                if (choice == 0) {
                    game.messageAll("trashing " + card.htmlName());
                    game.addToTrash(card);
                } else if (choice == 1) {
                    game.messageAll("discarding " + card.htmlName());
                    player.addToDiscard(card);
                } else { // choice == 2
                    toPutBack.add(card);
                }
            }
            if (!toPutBack.isEmpty()) {
                if (toPutBack.size() == 1) {
                    // automatically put the remaining card back (no need to prompt the user for the order)
                    Card card = toPutBack.get(0);
                    game.message(player, "putting " + card.htmlName() + " back on top");
                    game.messageOpponents(player, "putting 1 card back on top");
                    player.putOnDraw(card);
                } else { // toPutBack.size() == 2
                    putOnDeckInAnyOrder(player, game, toPutBack, "Sentry: Put the remaining cards on top of your deck");
                }
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, player.username + "'s deck is empty");
        }
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
