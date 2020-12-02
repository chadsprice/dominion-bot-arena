package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Workshop extends Card {

	@Override
	public String name() {
		return "Workshop";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[]{"Gain a card costing up_to 4$."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		gainCardCostingUpTo(player, game, 4);
	}

}
