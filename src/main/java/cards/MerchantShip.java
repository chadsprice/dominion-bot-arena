package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class MerchantShip extends Card {

	@Override
	public String name() {
		return "Merchant Ship";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.DURATION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {"Now and at the_start of your next_turn: <+2$>."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		plusCoins(player, game, 2);
	}

}
