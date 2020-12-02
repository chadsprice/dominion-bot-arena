package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class ThroneRoom extends Card {

	@Override
	public String name() {
		return "Throne Room";
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
        return new String[] {"You may play an_Action card from your_hand twice."};
    }

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		return onThroneRoomVariant(player, game, 2, false, hasMoved);
	}

}
