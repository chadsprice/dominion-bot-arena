package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class ThroneRoomFirstEdition extends Card {

    @Override
    public String name() {
        return "Throne Room (1st ed.)";
    }

    @Override
    public String plural() {
        return "Throne Rooms (1st ed.)";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Choose an action card in your_hand.",
                "Play it twice."
        };
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        return onThroneRoomVariant(player, game, 2, true, hasMoved);
    }

}
