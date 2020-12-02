package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class PoorHouse extends Card {

    @Override
    public String name() {
        return "Poor House";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 1;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+4$>",
                "Reveal your hand. <-1$>_per Treasure card in your_hand, to a minimum of_0$."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        revealHand(player, game);
        int numTreasures = (int) player.getHand().stream()
                .filter(Card::isTreasure)
                .count();
        // +$4, then -$1 per treasure in hand, but not less than $0
        int numCoins = Math.max(4 - numTreasures, 0);
        plusCoins(player, game, numCoins);
    }

}
