package cards;

import server.*;

import java.util.Set;

public class Hovel extends Card {

    @Override
    public String name() {
        return "Hovel";
    }

    @Override
    public Set<Type> types() {
        return types(Type.SHELTER);
    }

    @Override
    public String htmlType() {
        return "Reaction-Shelter";
    }

    @Override
    public String htmlHighlightType() {
        return "reaction-shelter";
    }

    @Override
    public int cost() {
        return 1;
    }

    @Override
    public String[] description() {
        return new String[] {"When you buy a_Victory card, you may trash_this from_your_hand."};
    }

    public boolean chooseTrash(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).hovelTrash();
        }
        int choice = new Prompt(player, game)
                .type(Prompt.Type.REACTION)
                .message(this.toString() + ": Trash " + this.htmlName() + " from your hand?")
                .multipleChoices(new String[] {"Trash", "Don't"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}
