package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Forager extends Card {

    @Override
    public String name() {
        return "Forager";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Action>",
                "<+1_Buy>",
                "Trash a_card from your_hand.",
                "<+1$>_per differently_named Treasure in the_trash."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        plusBuys(player, game, 1);
        if (!player.getHand().isEmpty()) {
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": Choose a card to trash."
            );
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
        } else {
            game.messageAll("having nothing in hand to trash");
        }
        // +$1 per differently named treasure in the trash
        int coins = (int) game.getTrash().stream()
                .collect(Collectors.toSet()).stream()
                .filter(Card::isTreasure)
                .count();
        plusCoins(player, game, coins);
    }

}
