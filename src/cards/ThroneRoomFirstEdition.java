package cards;

import server.Card;
import server.Game;
import server.Player;

public class ThroneRoomFirstEdition extends Card {

    public ThroneRoomFirstEdition() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        return onThroneRoomVariant(player, game, 2, true, hasMoved);
    }

    @Override
    public String[] description() {
        return new String[] {"Choose an action card in your hand.", "Play it twice."};
    }

    @Override
    public String toString() {
        return "Throne Room (1st ed.)";
    }

    @Override
    public String plural() {
        return "Throne Rooms (1st ed.)";
    }

}
