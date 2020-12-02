package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Woodcutter extends Card {

	@Override
	public String name() {
		return "Woodcutter";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[]{
				"<+1_Buy>",
				"<+2$>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
	}

}
