package cards;

import java.util.HashSet;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Upgrade extends Card {

	public Upgrade() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		onRemodelVariant(player, game, 1, true);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Trash a card from your hand.", "Gain a card costing exactly $1 more than it."};
	}

	@Override
	public String toString() {
		return "Upgrade";
	}

}
