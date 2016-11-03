package cards;

import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;

public class ThroneRoom extends Card {

	public ThroneRoom() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		return onThroneRoomVariant(player, game, 2, false, hasMoved);
	}

	@Override
	public String[] description() {
		return new String[] {"You may play an Action card from your hand twice."};
	}

	@Override
	public String toString() {
		return "Throne Room";
	}

}
