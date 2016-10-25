package cards;

import server.Card;
import server.Game;
import server.Player;

public class RuinedVillage extends Card {

    public RuinedVillage() {
        isAction = true;
        isRuins = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action"};
    }

    @Override
    public String toString() {
        return "Ruined Village";
    }

}
