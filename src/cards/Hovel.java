package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Hovel extends Card {

    @Override
    public int cost() {
        return 1;
    }

    public boolean chooseTrash(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).hovelChooseTrash();
        }
        int choice = game.promptMultipleChoice(player, "Hovel: Trash " + this.htmlName() + " from your hand?", "reactionPrompt", new String[] {"Trash", "Don't"});
        return (choice == 0);
    }

    @Override
    public String htmlClass() {
        return "reaction-shelter";
    }

    @Override
    public String htmlType() {
        return "Reaction-Shelter";
    }

    @Override
    public String[] description() {
        return new String[] {"When you buy a Victory card, you may trash this from your hand."};
    }

    @Override
    public String toString() {
        return "Hovel";
    }

}
