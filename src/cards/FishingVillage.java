package cards;

import server.Card;
import server.Game;
import server.Player;

public class FishingVillage extends Card {

	public FishingVillage() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 3;
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

	@Override
	public String[] description() {
		return new String[] {"+2 Actions", "+$1", "At the start of your next turn:", "+1 Action", "+$1"};
	}

	@Override
	public String toString() {
		return "Fishing Village";
	}

}
