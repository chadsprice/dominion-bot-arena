package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Survivors extends Card {

    public Survivors() {
        isAction = true;
        isRuins = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        List<Card> drawn = player.takeFromDraw(2);
        if (!drawn.isEmpty()) {
            game.message(player, "drawing " + Card.htmlList(drawn));
            game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
            if (chooseDiscard(player, game, drawn)) {
                game.message(player, "discarding them");
                game.messageOpponents(player, "discarding " + Card.htmlList(drawn));
                player.addToDiscard(drawn);
            } else {
                game.messageAll("putting them back");
                putOnDeckInAnyOrder(player, game, drawn, "Survivors: Put them back in any order");
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean chooseDiscard(Player player, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            return ((Bot) player).survivorsDiscard(cards);
        }
        int choice = game.promptMultipleChoice(player, "Survivors: You draw " + Card.htmlList(cards) + ". Discard them or put them back?", new String[] {"Discard", "Put back"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"Look at the top 2 cards of your deck.", "Discard them or put them back in any order."};
    }

    @Override
    public String toString() {
        return "Survivors";
    }

    @Override
    public String plural() {
        return toString();
    }

}
