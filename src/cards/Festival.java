package cards;

import server.Card;
import server.Game;
import server.Player;

public class Festival extends Card {

	public Festival() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 2);
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
	}

	@Override
	public String[] description() {
		return new String[]{"+2 Actions", "+1 Buy", "+$2"};
	}

	@Override
	public String toString() {
		return "Festival";
	}

}
