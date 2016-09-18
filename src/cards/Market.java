package cards;

import server.Card;
import server.Game;
import server.Player;

public class Market extends Card {

	public Market() {
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
		plusBuys(player, game, 1);
		plusCoins(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[]{"+1 Card", "+1 Action", "+1 Buy", "+$1"};
	}

	@Override
	public String toString() {
		return "Market";
	}

}
