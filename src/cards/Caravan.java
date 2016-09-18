package cards;

import server.Card;
import server.Duration;
import server.Game;
import server.Player;

public class Caravan extends Card {

	public Caravan() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		plusCards(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "At the start of your next turn, +1 Card."};
	}

	@Override
	public String toString() {
		return "Caravan";
	}

}
