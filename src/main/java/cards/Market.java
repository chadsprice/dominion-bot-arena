package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Market extends Card {

	@Override
	public String name() {
		return "Market";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{
				"<+1_Card>",
				"<+1_Action>",
				"<+1_Buy>",
				"<+1$>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		plusBuys(player, game, 1);
		plusCoins(player, game, 1);
	}

}
