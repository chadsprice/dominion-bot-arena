package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class JunkDealer extends Card {

    @Override
    public String name() {
        return "Junk Dealer";
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
                "<+1_Card>",
                "<+1_Action>",
                "<+1$>",
                "Trash a_card from your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusCoins(player, game, 1);
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
            game.messageAll("revealing an empty hand, trashing nothing");
        }
    }

}
