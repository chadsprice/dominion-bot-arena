package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.stream.Collectors;

public class Forager extends Card {

    public Forager() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        plusBuys(player, game, 1);
        if (!player.getHand().isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()), "Forager: Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
        } else {
            game.messageAll("having nothing in hand to trash");
        }
        // +$1 per differently named treasure in the trash
        int coins = (int) game.getTrash().stream().collect(Collectors.toSet()).stream().filter(c -> c.isTreasure).count();
        plusCoins(player, game, coins);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "+1 Buy", "Trash a card from your hand.", "+$1 per differently named Treasure in the trash."};
    }

    @Override
    public String toString() {
        return "Forager";
    }

}
