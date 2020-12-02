package cards;

import server.*;

import java.util.Set;
import java.util.stream.Collectors;

public class SpiceMerchant extends Card {

    @Override
    public String name() {
        return "Spice Merchant";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "You may trash a_Treasure from your_hand. If_you_do, choose_one:",
                "* <+2_Cards> and <+1_Action>",
                "* <+2$> and <+1_Buy>"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> trashable = player.getHand().stream()
                .filter(Card::isTreasure)
                .collect(Collectors.toSet());
        if (!trashable.isEmpty()) {
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    trashable,
                    this.toString() + ": You may trash a treasure from your hand.",
                    "Trash nothing"
            );
            if (toTrash != null) {
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                if (chooseFirstBenefit(player, game)) {
                    plusCards(player, game, 2);
                    plusActions(player, game, 1);
                } else {
                    plusCoins(player, game, 2);
                    plusBuys(player, game, 1);
                }
            } else {
                game.messageAll("trashing nothing");
            }
        } else {
            game.messageAll("having no treasure to trash");
        }
    }

    private boolean chooseFirstBenefit(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).spiceMerchantFirstBenefit();
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Choose one")
                .multipleChoices(new String[] {"+2 Cards and +1 Action", "+$2 and +1 Buy"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}
