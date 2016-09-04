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
		// +2 actions
		player.addActions(2);
		// +1 buy
		player.addBuys(1);
		// +$2
		player.addExtraCoins(2);
		game.message(player, "... You get +2 actions, +1 buy, and +$2");
		game.messageOpponents(player, "... getting +2 actions, +1 buy, and +$2");
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
