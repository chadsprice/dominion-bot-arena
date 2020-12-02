package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Caravan extends Card {

	@Override
	public String name() {
		return "Caravan";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.DURATION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Card>",
				"<+1_Action>",
				"At_the_start of your_next_turn, <+1_Card>."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		plusCards(player, game, 1);
	}

}
