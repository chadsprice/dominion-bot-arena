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
		// +1 buy
		player.addBuys(1);
		game.messageAll("getting +1 buy");
		// +$2
		player.addExtraCoins(2);
		game.messageAll("getting +$2");
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
