package cards;

import server.Card;
import server.Game;
import server.Player;

public class Remodel extends Card {

	public Remodel() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		onRemodelVariant(player, game, 2, false);
	}

	@Override
	public String[] description() {
		return new String[]{"Trash a card from your hand.", "Gain a card costing up to $2 more than it."};
	}

	@Override
	public String toString() {
		return "Remodel";
	}

}
