package cards;

import server.*;

import java.util.*;
import java.util.stream.Collectors;

public class Lurker extends Card {

    @Override
    public String name() {
        return "Lurker";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Action>",
                "Choose_one:",
                "* Trash an Action card from the_supply",
                "* Gain an Action card from the_trash"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        if (chooseTrashOverGain(player, game))  {
            Set<Card> trashable = game.cardsInSupply().stream()
                    .filter(Card::isAction)
                    .collect(Collectors.toSet());
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
            gainFromTrashSatisfying(
                    player,
                    game,
                    Card::isAction,
                    this.toString() + ": Choose an action card to gain from the trash."
            );
        }
    }

    private boolean chooseTrashOverGain(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).lurkerTrashOverGain();
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Trash an action card from the supply, or gain an action card from the trash?")
                .multipleChoices(new String[] {"Trash", "Gain"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private Card chooseTrashFromSupply(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).lurkerTrashFromSupply(Collections.unmodifiableSet(trashable));
            checkContains(trashable, toTrash);
            return toTrash;
        }
        return new Prompt(player, game)
                .message(this.toString() + ": Choose an action card from the supply to trash.")
                .supplyChoices(trashable)
                .responseCard();
    }

}
