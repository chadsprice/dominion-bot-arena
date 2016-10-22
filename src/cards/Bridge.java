package cards;

import server.Card;
import server.Game;
import server.Player;

public class Bridge extends Card {

	public Bridge() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		plusCoins(player, game, 1);
		game.messageAll("cards cost $1 less this turn");
		game.addCardCostReduction(1);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "+$1", "This turn, cards (everywhere) cost $1 less, but not less than $0."};
	}

	@Override
	public String toString() {
		return "Bridge";
	}

}
