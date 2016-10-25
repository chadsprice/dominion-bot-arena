package cards;

import server.Card;
import server.Game;
import server.Player;

public class AbandonedMine extends Card {

    public AbandonedMine() {
        isAction = true;
        isRuins = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+$1"};
    }

    @Override
    public String toString() {
        return "Abandoned Mine";
    }

}
