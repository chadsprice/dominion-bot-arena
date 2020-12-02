package cards;

import server.*;

import java.util.Set;
import java.util.stream.Collectors;

public class Graverobber extends Card {

    @Override
    public String name() {
        return "Graverobber";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Choose_one:",
                "* Gain a_card from the_trash costing from_$3 to_$6, putting_it on_top of your_deck",
                "* Trash an_Action card from your_hand and gain a_card costing up_to 3$_more than_it"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        if (chooseGain(player, game)) {
            // gain a card from the trash costing between 3 and 6
            gainFromTrashSatisfying(
                    player,
                    game,
                    c -> 3 <= c.cost(game) && c.cost(game) <= 6,
                    this.toString() + ": Choose a card to gain from the trash costing from $3 to $6, putting it on top of your deck.",
                    true
            );
        } else {
            // trash an action card from your hand and gain a card costing up to $3 more
            Set<Card> trashable = player.getHand().stream()
                    .filter(Card::isAction)
                    .collect(Collectors.toSet());
            if (!trashable.isEmpty()) {
                Card toTrash = promptChooseTrashFromHand(
                        player,
                        game,
                        trashable,
                        this.toString() + ": Choose an action card to trash."
                );
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 3);
                if (!gainable.isEmpty()) {
                    Card toGain = promptChooseGainFromSupply(
                            player,
                            game,
                            gainable,
                            this.toString() + ": Choose a card to gain."
                    );
                    game.messageAll("gaining " + toGain.htmlName());
                    game.gain(player, toGain);
                } else {
                    game.messageAll("gaining nothing");
                }
            } else {
                game.messageAll("having no action card to trash");
            }
        }
    }

    private boolean chooseGain(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).graverobberGain();
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Choose one")
                .multipleChoices(new String[] {"Gain from trash", "Upgrade"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}
