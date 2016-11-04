package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.*;

public class Lurker extends Card {

    public Lurker() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        if (chooseTrashOverGain(player, game))  {
            Set<Card> trashable = game.cardsInSupply();
            if (!trashable.isEmpty()) {
                Card toTrash = chooseTrashFromSupply(player, game, trashable);
                game.messageAll("trashing " + toTrash.htmlName() + " from the supply");
                game.takeFromSupply(toTrash);
                // this doesn't trigger Market Square because the card your are trashing isn't "yours"
                game.trash(player, toTrash, false);
            } else {
                game.messageAll("there are no action cards in the supply to trash");
            }
        } else {
            // gain an action card from the trash
            gainFromTrashSatisfying(player, game,
                    c -> c.isAction,
                    this.toString() + ": Choose an action card to gain from the trash.");
        }
    }

    private boolean chooseTrashOverGain(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).lurkerTrashOverGain();
        }
        int choice = game.promptMultipleChoice(player, this.toString() + ": Trash an action card from the supply, or gain an action card from the trash?", new String[] {"Trash", "Gain"});
        return (choice == 0);
    }

    private Card chooseTrashFromSupply(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).lurkerTrashFromSupply(trashable);
            if (!trashable.contains(toTrash)) {
                throw new IllegalStateException();
            }
            return toTrash;
        }
        return game.promptChooseGainFromSupply(player, trashable, this.toString() + ": Choose an action card from the supply to trash.");
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "Choose one: Trash an Action card from the supply; or gain an Action card from the trash."};
    }

    @Override
    public String toString() {
        return "Lurker";
    }

}
