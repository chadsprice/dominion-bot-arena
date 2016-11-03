package cards;

import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;

public class KingsCourt extends Card {

	public KingsCourt() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 7;
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		return onThroneRoomVariant(player, game, 3, false, hasMoved);
	}

	@Override
	public String[] description() {
		return new String[] {"You may choose an action card in your hand. Play it three times."};
	}

	@Override
	public String toString() {
		return "King's Court";
	}

}
