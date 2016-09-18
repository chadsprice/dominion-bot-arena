package cards;

import server.Card;
import server.Game;
import server.Player;

public class Woodcutter extends Card {

	public Woodcutter() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
	}

	@Override
	public String[] description() {
		return new String[]{"+1 Buy", "+$2"};
	}

	@Override
	public String toString() {
		return "Woodcutter";
	}

}
