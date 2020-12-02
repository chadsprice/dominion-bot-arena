package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Bridge extends Card {

	@Override
	public String name() {
		return "Bridge";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Buy>",
				"<+1$>",
				"This_turn, cards_(everywhere) cost_1$_less, but not less_than_0$."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		plusCoins(player, game, 1);
		game.messageAll("cards cost $1 less this turn");
		game.cardCostReduction += 1;
	}

}
