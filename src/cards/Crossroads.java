package cards;

import server.Card;
import server.Game;
import server.Player;

public class Crossroads extends Card {

    public Crossroads() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        revealHand(player, game);
        // +1 card per victory card revealed
        int numVictoryCardsInHand = (int) player.getHand().stream().filter(c -> c.isVictory).count();
        plusCards(player, game, numVictoryCardsInHand);
        // if this is the first crossroad this turn, +3 actions
        if (!game.playedCrossroadsThisTurn) {
            plusActions(player, game, 3);
            game.playedCrossroadsThisTurn = true;
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Reveal your hand.", "+1 Card per Victory card revealed.", "If this is the first time you played a Crossroads this turn, +3 Actions."};
    }

    @Override
    public String toString() {
        return "Crossroads";
    }

    @Override
    public String plural() {
        return toString();
    }

}
