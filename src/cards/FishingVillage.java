package cards;

import server.Card;
import server.Duration;
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
		// +2 actions
		player.addActions(2);
		game.messageAll("getting +2 actions");
		// +$1
		player.addExtraCoins(1);
		game.messageAll("getting +$1");
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		// +1 action
		player.addActions(1);
		game.messageAll("getting +1 action");
		// +$1
		player.addExtraCoins(1);
		game.messageAll("getting +$1");
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
