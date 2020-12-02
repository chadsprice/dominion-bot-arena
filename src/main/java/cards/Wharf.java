package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Wharf extends Card {

	@Override
	public String name() {
		return "Wharf";
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
		return new String[] {
				"Now and at the_start of your next_turn:",
				"<+2_Cards>",
				"<+1_Buy>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
		plusBuys(player, game, 1);
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		plusCards(player, game, 2);
		plusBuys(player, game, 1);
	}

}
