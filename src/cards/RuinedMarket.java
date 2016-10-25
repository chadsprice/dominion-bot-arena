package cards;

import server.Card;
import server.Game;
import server.Player;

public class RuinedMarket extends Card {

    public RuinedMarket() {
        isAction = true;
        isRuins = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Buy"};
    }

    @Override
    public String toString() {
        return "Ruined Market";
    }

}
