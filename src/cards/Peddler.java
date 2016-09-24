package cards;

import server.Card;
import server.Game;
import server.Player;

public class Peddler extends Card {

	public Peddler() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 8;
	}

	@Override
	public int cost(Game game) {
		int cost = 8;
		if (game.inBuyPhase) {
			cost -= 2 * game.numActionsInPlay();
		}
		return Math.max(cost, 0);
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		plusCoins(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "+$1", "During your buy phase, this costs $2 less per Action card you have in play, but not less than $0."};
	}

	@Override
	public String toString() {
		return "Peddler";
	}

}
