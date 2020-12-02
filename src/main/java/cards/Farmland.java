package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class Farmland extends Card {

    @Override
    public String name() {
        return "Farmland";
    }

    @Override
    public Set<Type> types() {
        return types(Type.VICTORY);
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public String[] description() {
        return new String[] {
                "2_VP",
                "When you buy_this, trash a_card from your_hand.", "Gain a_card costing exactly 2$_more than the_trashed_card."
        };
    }

    @Override
    public int victoryValue() {
        return 2;
    }

    public void onBuy(Player player, Game game) {
        if (!player.getHand().isEmpty()) {
            // trash a card from your hand
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": Choose a card to trash and gain a card costing exactly $2 more."
            );
            game.messageAll("trashing " + toTrash.htmlName() + " because of " + this.htmlNameRaw());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain a card costing exactly $2 more
            Set<Card> gainable = game.cardsCostingExactly(toTrash.cost(game) + 2);
            if (!gainable.isEmpty()) {
                Card toGain = promptChooseGainFromSupply(
                        player,
                        game,
                        gainable,
                        this.toString() + ": Choose a card to gain."
                );
                game.messageIndent++;
                game.messageAll("gaining " + toGain.htmlName());
                game.gain(player, toGain);
                game.messageIndent--;
            }
        }
    }

}
