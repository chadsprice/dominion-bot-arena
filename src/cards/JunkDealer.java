package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;

public class JunkDealer extends Card {

    public JunkDealer() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusCoins(player, game, 1);
        if (!player.getHand().isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()), "Junk Dealer: Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.addToTrash(player, toTrash);
        } else {
            game.messageAll("revealing an empty hand, trashing nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "+$1", "Trash a card from your hand."};
    }

    @Override
    public String toString() {
        return "Junk Dealer";
    }

}
