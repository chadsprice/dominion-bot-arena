package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.stream.Collectors;

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
            game.messageAll("drawing " + Card.htmlList(revealed));
            game.messageIndent++;
            // put the revealed victory cards any Curses into your hand
            List<Card> toPutInHand = revealed.stream()
                    .filter(c -> c.isVictory || c == Cards.CURSE)
                    .collect(Collectors.toList());
            if (!toPutInHand.isEmpty()) {
                game.message(player, "putting " + Card.htmlList(toPutInHand) + " into your hand");
                game.messageOpponents(player, "putting " + Card.htmlList(toPutInHand) + " into their hand");
                toPutInHand.forEach(revealed::remove);
                player.addToHand(toPutInHand);
            }
            // put the rest back in any order
            if (!revealed.isEmpty()) {
                game.messageAll("putting the rest back");
                putOnDeckInAnyOrder(player, game, revealed, this.toString() + ": Put the rest back in any order.");
            }
            game.messageIndent--;
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
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
