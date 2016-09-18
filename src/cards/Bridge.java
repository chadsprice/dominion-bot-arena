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
		
		game.addCardCostReduction(1);
		game.messageAll("all cards cost $1 less this turn");
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "+$1", "All cards (including cards in players' hands) cost $1 less this turn, but not less than $0. "};
	}

	@Override
	public String toString() {
		return "Bridge";
	}

}
