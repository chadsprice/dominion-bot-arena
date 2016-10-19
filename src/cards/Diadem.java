package cards;

import server.Card;
import server.Game;
import server.Player;

public class Diadem extends Card {

    public Diadem() {
        isTreasure = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public int treasureValue() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // +$1 per unused action
        plusCoins(player, game, player.getActions());
    }

    @Override
    public String[] description() {
        return new String[] {"$2", "When you play this, +$1 per unused Action you have (Action, not Action card).", "(This is not in the Supply.)"};
    }

    @Override
    public String toString() {
        return "Diadem";
    }

    @Override
    public String htmlType() {
        return "Treasure-Prize";
    }

}
