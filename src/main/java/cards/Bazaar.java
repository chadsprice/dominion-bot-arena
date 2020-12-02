package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Bazaar extends Card {

	@Override
	public String name() {
		return "Bazaar";
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
		return new String[] {"<+1_Card>", "<+2_Actions>", "<+1$>"};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 2);
		plusCoins(player, game, 1);
	}

}
