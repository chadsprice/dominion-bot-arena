package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Festival extends Card {

	@Override
	public String name() {
		return "Festival";
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
				"<+2_Actions>",
				"<+1_Buy>",
				"<+2$>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 2);
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
	}

}
