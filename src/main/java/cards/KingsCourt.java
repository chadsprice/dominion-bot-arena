package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class KingsCourt extends Card {

	@Override
	public String name() {
		return "King's Court";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 7;
	}

	@Override
	public String[] description() {
		return new String[] {"You may choose an Action card in your_hand. Play_it three_times."};
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		return onThroneRoomVariant(player, game, 3, false, hasMoved);
	}

}
