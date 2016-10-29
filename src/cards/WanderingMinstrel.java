package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WanderingMinstrel extends Card {

    public WanderingMinstrel() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
        // reveal the top 3 cards of your deck
        List<Card> drawn = player.takeFromDraw(3);
        if (!drawn.isEmpty()) {
            game.messageAll("drawing " + Card.htmlList(drawn));
            List<Card> actions = drawn.stream().filter(c -> c.isAction).collect(Collectors.toList());
            List<Card> rest = drawn.stream().filter(c -> !c.isAction).collect(Collectors.toList());
            // put the actions back on top in any order
            if (!actions.isEmpty()) {
                game.messageAll("putting " + Card.htmlList(actions) + " back on top");
                putOnDeckInAnyOrder(player, game, actions, "Wandering Minstrel: Put the actions back on top in any order.");
            }
            // discard the rest
            if (!rest.isEmpty()) {
                game.messageAll("discarding the rest");
                player.addToDiscard(rest);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+2 Actions", "Reveal the top 3 cards of your deck.", "Put the actions back on top in any order and discard the rest."};
    }

    @Override
    public String toString() {
        return "Wandering Minstrel";
    }

}
