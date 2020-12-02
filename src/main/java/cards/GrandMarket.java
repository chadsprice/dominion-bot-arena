package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class GrandMarket extends Card {

	@Override
	public String name() {
		return "Grand Market";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Card>",
				"<+1_Action>",
				"<+1_Buy>",
				"<+2$>",
				"You can't buy_this if you have any [Copper] in_play."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
	}

}
