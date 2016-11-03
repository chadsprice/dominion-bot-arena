package cards;

import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Workshop extends Card {

	public Workshop() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		gainCardCostingUpTo(player, game, 4);
	}

	@Override
	public String[] description() {
		return new String[]{"Gain a card costing up to $4."};
	}

	@Override
	public String toString() {
		return "Workshop";
	}

}
