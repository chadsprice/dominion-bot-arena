package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Vassal extends Card {

    public Vassal() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
        // reveal the top card of your deck; if it's an action, you may play it, otherwise discard it
        List<Card> drawn = player.takeFromDraw(1);
        if (!drawn.isEmpty()) {
            Card card = drawn.get(0);
            boolean playing = false;
            if (card.isAction) {
                int choice = game.promptMultipleChoice(player, "Vassal: You draw " + card.htmlName() + ". Play it?", new String[] {"Play", "Discard"});
                if (choice == 0) {
                    playing = true;
                }
            }
            if (playing) {
                game.messageAll("drawing and playing " + card.htmlName());
                player.putFromHandIntoPlay(card);
                game.playAction(player, card, false);
            } else {
                game.messageAll("drawing and discarding " + card.htmlName());
                player.addToDiscard(card);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+$2", "Discard the top card of your deck. If it's an Action card, you may play it."};
    }

    @Override
    public String toString() {
        return "Vassal";
    }

}
