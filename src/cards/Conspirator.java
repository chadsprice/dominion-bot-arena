package cards;

import server.Card;
import server.Game;
import server.Player;

public class Conspirator extends Card {

	public Conspirator() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
		// if you played at least 3 actions this turn, +1 card, +1 action
		if (game.actionsPlayedThisTurn >= 3) {
			plusCards(player, game, 1);
			plusActions(player, game, 1);
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "If you've played 3 or more Actions this turn (counting this), +1 Card and +1 Action."};
	}

	@Override
	public String toString() {
		return "Conspirator";
	}
}
