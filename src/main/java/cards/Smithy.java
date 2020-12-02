package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Smithy extends Card {

	@Override
	public String name() {
		return "Smithy";
	}

	@Override
	public String plural() {
		return "Smithies";
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
		return new String[]{"<+3_Cards>"};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 3);
	}

}
