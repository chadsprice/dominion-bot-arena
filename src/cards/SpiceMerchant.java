package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class SpiceMerchant extends Card {

    public SpiceMerchant() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> trashable = player.getHand().stream().filter(c -> c.isTreasure).collect(Collectors.toSet());
        if (!trashable.isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, trashable, "Spice Merchant: You may trash a treasure from your hand.", false, "Trash nothing");
            if (toTrash != null) {
                player.removeFromHand(toTrash);
                game.addToTrash(player, toTrash);
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
        int choice = game.promptMultipleChoice(player, "Spice Merchant: Choose one", new String[] {"+2 Cards and +1 Action", "+$2 and +1 Buy"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"You may trash a Treasure from your hand. If you do, choose one:", "+2 Cards and +1 Action;", "or +$2 and +1 Buy."};
    }

    @Override
    public String toString() {
        return "Spice Merchant";
    }

}
