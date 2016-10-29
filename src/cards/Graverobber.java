package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.stream.Collectors;

public class Graverobber extends Card {

    public Graverobber() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        if (chooseGain(player, game)) {
            // gain a card from the trash costing between 3 and 6
            gainFromTrashSatisfying(player, game,
                    c -> 3 <= c.cost(game) && c.cost(game) <= 6,
                    "Graverobber: Choose a card to gain from the trash costing from $3 to $6, putting it on top of your deck.",
                    true);
        } else {
            // trash an action card from your hand and gain a card costing up to $3 more
            upgradeViaTrashing(player, game,
                    player.getHand().stream().filter(c -> c.isAction).collect(Collectors.toSet()),
                    3,
                    "Graverobber: Choose an action card to trash.",
                    "Graverobber: Choose a card to gain.");
        }
    }

    private boolean chooseGain(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).graverobberGain();
        }
        int choice = game.promptMultipleChoice(player, "Graverobber: Choose one", new String[] {"Gain from trash", "Upgrade"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"Choose one: Gain a card from the trash costing from $3 to $6, putting it on top of your deck; or trash an Action card from your hand and gain a card costing up to $3 more than it."};
    }

    @Override
    public String toString() {
        return "Graverobber";
    }

}
