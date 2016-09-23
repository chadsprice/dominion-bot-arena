package cards;

import server.Card;
import server.Game;
import server.Player;

public class GrandMarket extends Card {

	public GrandMarket() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "+1 Buy", "+$2", "You can't buy this if you have any Copper in play."};
	}

	@Override
	public String toString() {
		return "Grand Market";
	}

}
