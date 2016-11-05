package cards;

import server.Card;
import server.Game;
import server.Player;

public class Diplomat extends Card {

    public Diplomat() {
        isAction = true;
        isAttackReaction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        if (player.getHand().size() <= 5) {
            plusActions(player, game, 2);
        }
    }

    @Override
    public boolean onAttackReaction(Player player, Game game) {
        plusCards(player, game, 2);
        discardNumber(player, game, 3);
        return false;
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "If you have 5 or fewer cards in hand (after drawing), +2 Actions.", "When another player plays an Attack card, you may first reveal this from a hand of 5 or more cards, to draw 2 cards then discard 3."};
    }

    @Override
    public String toString() {
        return "Diplomat";
    }

}
