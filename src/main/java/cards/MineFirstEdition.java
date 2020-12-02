package cards;

import server.*;

import java.util.Set;
import java.util.stream.Collectors;

public class MineFirstEdition extends Card {

    @Override
    public String name() {
        return "Mine (1st ed.)";
    }

    @Override
    public String plural() {
        return "Mines (1st ed.)";
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
        return new String[]{
                "Trash a_Treasure card from your_hand.",
                "Gain a_Treasure card costing up_to 3$_more; put_it into_your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        // trash a treasure from your hand
        Set<Card> treasures = player.getHand().stream()
                .filter(Card::isTreasure)
                .collect(Collectors.toSet());
        if (!treasures.isEmpty()) {
            // trashing a treasure is mandatory for the first edition of Mine
            Card toTrash = chooseTrash(player, game, treasures);
            // trash the chosen treasure
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain a treasure costing up to $3 more
            Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost() + 3).stream()
                    .filter(Card::isTreasure)
                    .collect(Collectors.toSet());
            if (!gainable.isEmpty()) {
                Card toGain = promptChooseGainFromSupply(
                        player,
                        game,
                        gainable,
                        this.toString() + ": Gain a treasure to your hand costing up to $3 more."
                );
                game.message(player, "gaining " + toGain.htmlName() + " to your hand");
                game.messageOpponents(player, "gaining " + toGain.htmlName() + " to their hand");
                game.gainToHand(player, toGain);
            } else {
                game.messageAll("gaining nothing");
            }
        } else {
            game.messageAll("having no treasure to trash");
        }
    }

    private Card chooseTrash(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).mineFirstEditionTrash(trashable);
            checkContains(trashable, toTrash);
            return toTrash;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may trash a treasure from your hand.")
                .responseCard();
    }

}
