package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Remodel extends Card {

	@Override
	public String name() {
		return "Remodel";
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
		return new String[]{
				"Trash a card from your_hand.",
				"Gain a card costing up_to 2$_more than_it."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		onRemodelVariant(player, game, 2, false);
	}

}
