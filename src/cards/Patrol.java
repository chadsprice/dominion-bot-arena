package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Patrol extends Card {

    public Patrol() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 3);
        // reveal 4 cards, etc.
        List<Card> revealed = player.takeFromDraw(4);
        if (!revealed.isEmpty()) {
            game.messageAll("revealing " + Card.htmlList(revealed));
            // put the revealed victory cards any curses into your hand
            List<Card> toPutInHand = new ArrayList<Card>();
            for (Iterator<Card> iter = revealed.iterator(); iter.hasNext(); ) {
                Card next = iter.next();
                if (next.isVictory || next == Card.CURSE) {
                    iter.remove();
                    toPutInHand.add(next);
                }
            }
            if (!toPutInHand.isEmpty()) {
                game.message(player, "putting " + Card.htmlList(toPutInHand) + " into your hand");
                game.messageOpponents(player, "putting " + Card.htmlList(toPutInHand) + " into their hand");
                player.addToHand(toPutInHand);
            }
            // put the rest back in any order
            if (!revealed.isEmpty()) {
                game.messageAll("putting the rest back");
                putOnDeckInAnyOrder(player, game, revealed, "Patrol: Put the rest back in any order");
            }
        } else {
            game.message(player, "revealing nothing because your deck is empty");
            game.messageOpponents(player, "revealing nothing because their deck is empty");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+3 Cards", "Reveal the top 4 cards of your deck. Put the Victory cards and Curses into your hand. Put the rest back in any order."};
    }

    @Override
    public String toString() {
        return "Patrol";
    }

}
