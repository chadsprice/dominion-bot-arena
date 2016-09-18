package cards;

import server.Card;
import server.Game;
import server.Player;

public class Village extends Card {

	public Village() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 2);
	}

	@Override
	public String[] description() {
		return new String[]{"+1 Card", "+2 Actions"};
	}

	@Override
	public String toString() {
		return "Village";
	}

}
