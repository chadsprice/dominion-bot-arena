package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class FishingVillage extends Card {

	@Override
	public String name() {
		return "Fishing Village";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.DURATION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2_Actions>",
				"<+1$>",
				"At the start of your next turn:",
				"<+1_Action>",
				"<+1$>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 2);
		plusCoins(player, game, 1);
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		plusActions(player, game, 1);
		plusCoins(player, game, 1);
	}

}
