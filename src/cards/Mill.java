package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Mill extends Card {

    public Mill() {
        isAction = true;
        isVictory = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public int victoryValue() {
        return 1;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        if (!player.getHand().isEmpty()) {
            String promptMessage = "Mill: Discard 2 cards for +$2?";
            if (player.getHand().size() == 1) {
                promptMessage = "Discard your last card? (You will not get +$2)";
            }
            int choice = game.promptMultipleChoice(player, promptMessage, new String[] {"Yes", "No"});
            if (choice == 0) {
                List<Card> toDiscard = game.promptDiscardNumber(player, 2, "Mill", "attackPrompt");
                game.messageAll("discarding " + Card.htmlList(toDiscard) + " for +$2");
                player.putFromHandIntoDiscard(toDiscard);
                player.addCoins(2);
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "You may discard 2 cards, for +$2.", "1 VP"};
    }

    @Override
    public String toString() {
        return "Mill";
    }

}
