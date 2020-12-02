package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Laboratory extends Card {

	@Override
	public String name() {
		return "Laboratory";
	}

	@Override
	public String plural() {
		return "Laboratories";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{
				"<+2_Cards>",
				"<+1_Action>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
		plusActions(player, game, 1);
	}

}
