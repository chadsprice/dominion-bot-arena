package cards;

import server.Card;
import server.Game;
import server.Player;

public class Merchant extends Card {

    public Merchant() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "The first time you play a Silver this turn, +$1."};
    }

    @Override
    public String toString() {
        return "Merchant";
    }

}
