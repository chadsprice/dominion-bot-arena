package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Upgrade extends Card {

	@Override
	public String name() {
		return "Upgrade";
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
		return new String[] {
				"<+1_Card>",
				"<+1_Action>",
				"Trash a card from your_hand.",
				"Gain a card costing exactly 1$_more than_it."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		onRemodelVariant(player, game, 1, true);
	}

}
