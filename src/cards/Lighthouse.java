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
		plusActions(player, game, 1);
		plusCoins(player, game, 1);
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		plusCoins(player, game, 1);
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
