package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Monument extends Card {

	@Override
	public String name() {
		return "Monument";
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
				"<+$2>",
				"<+1_VP>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
		plusVictoryTokens(player, game, 1);
	}

}
