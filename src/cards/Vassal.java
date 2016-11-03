package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

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
        Card top = topCardOfDeck(player);
        if (top != null) {
            game.messageAll("drawing " + top.htmlName());
            game.messageIndent++;
            if (top.isAction && choosePlay(player, game, top)) {
                player.putFromHandIntoPlay(top);
                game.playAction(player, top, false);
            } else {
                game.messageAll("discarding it");
            }
            game.messageIndent--;
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean choosePlay(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).vassalPlay(card);
        }
        int choice = game.promptMultipleChoice(player, "Vassal: You draw " + card.htmlName() + ". Play it?", new String[] {"Play", "Discard"});
        return (choice == 0);
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
