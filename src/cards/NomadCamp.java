package cards;

import server.Card;
import server.Game;
import server.Player;

public class NomadCamp extends Card {

    public NomadCamp() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        plusCoins(player, game, 2);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Buy", "+$2", "When you gain this, put it on top of your deck."};
    }

    @Override
    public String toString() {
        return "Nomad Camp";
    }

}
