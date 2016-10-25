package cards;

import server.Card;
import server.Game;
import server.Player;

public class Necropolis extends Card {

    public Necropolis() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 1;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 2);
    }

    @Override
    public String htmlClass() {
        return "action-shelter";
    }

    @Override
    public String htmlType() {
        return "Action-Shelter";
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Actions"};
    }

    @Override
    public String toString() {
        return "Necropolis";
    }

}
