package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Patrol extends Card {

    @Override
    public String name() {
        return "Patrol";
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
                "<+3_Cards>",
                "Reveal the_top 4_cards of your_deck. Put the_Victory cards and [Curses] into your_hand. Put the rest back in_any_order."
        };
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
                    .filter(c -> c.isVictory() || c == Cards.CURSE)
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

}
