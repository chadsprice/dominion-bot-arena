package cards;

import server.Card;
import server.Game;
import server.Player;

public class PoorHouse extends Card {

    public PoorHouse() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 1;
    }

    @Override
    public void onPlay(Player player, Game game) {
        revealHand(player, game);
        int numTreasures = (int) player.getHand().stream().filter(c -> c.isTreasure).count();
        // +$4, then -$1 per treasure in hand, but not less than $0
        int numCoins = Math.max(4 - numTreasures, 0);
        plusCoins(player, game, numCoins);
    }

    @Override
    public String[] description() {
        return new String[] {"+$4", "Reveal your hand. -$1 per Treasure card in your hand, to a minimum of $0."};
    }

    @Override
    public String toString() {
        return "Poor House";
    }

}
