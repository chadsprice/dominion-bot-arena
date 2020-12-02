package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class GreatHall extends Card {

	@Override
	public String name() {
		return "Great Hall";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.VICTORY);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Card>",
				"<+1_Action>",
				"1_VP"
		};
	}

	@Override
	public int victoryValue() {
		return 1;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
	}

}
