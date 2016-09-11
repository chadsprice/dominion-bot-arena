package cards;

import server.Card;
import server.Duration;
import server.Game;
import server.Player;

public class Lighthouse extends Card {

	public Lighthouse() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 action
		player.addActions(1);
		// +$1
		player.addExtraCoins(1);
		game.message(player, "... You  get +1 action and +$1");
		game.messageOpponents(player, "... getting +1 action and +$1");
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		// +$1
		player.addExtraCoins(1);
		game.message(player, "... You  get +$1");
		game.messageOpponents(player, "... getting +$1");
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Action", "Now and at the start of your next turn: +$1.", "While this is in play, when another player plays an Attack card, it doesn't affect you."};
	}

	@Override
	public String toString() {
		return "Lighthouse";
	}

}
