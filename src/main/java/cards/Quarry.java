package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Quarry extends Card {

	@Override
	public String name() {
		return "Quarry";
	}

	@Override
	public String plural() {
		return "Quarries";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[]{
				"1$",
				"While this is in_play, Action cards cost 2$_less, but not less_than_0$."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		game.messageAll("action cards cost $2 less while this is in play");
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

}
