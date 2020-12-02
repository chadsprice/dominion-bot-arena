package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Peddler extends Card {

	@Override
	public String name() {
		return "Peddler";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
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
	public String[] description() {
		return new String[] {
				"<+1_Card>",
				"<+1_Action>",
				"<+1$>",
				"During your buy_phase, this_costs 2$_less per_Action card you have in_play, but not less_than_0$."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		plusCoins(player, game, 1);
	}

}
