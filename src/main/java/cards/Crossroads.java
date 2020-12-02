package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Crossroads extends Card {

    @Override
    public String name() {
        return "Crossroads";
    }

    @Override
    public String plural() {
        return toString();
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Reveal your_hand.",
                "<+1_Card> per Victory_card revealed.",
                "If this is the first_time you played a_[Crossroads] this_turn, <+3_Actions>."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        revealHand(player, game);
        // +1 card per victory card revealed
        int numVictoryCardsInHand = (int) player.getHand().stream()
                .filter(Card::isVictory)
                .count();
        plusCards(player, game, numVictoryCardsInHand);
        // if this is the first crossroad this turn, +3 actions
        if (!game.playedCrossroadsThisTurn) {
            plusActions(player, game, 3);
            game.playedCrossroadsThisTurn = true;
        }
    }

}
