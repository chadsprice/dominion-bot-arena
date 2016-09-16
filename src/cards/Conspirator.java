package cards;

import java.util.List;

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
		// +2
		player.addExtraCoins(2);
		game.messageAll("getting +$2");
		// if you played at least 3 actions this turn, +1 card, +1 action
		if (game.actionsPlayedThisTurn >= 3) {
			List<Card> drawn = player.drawIntoHand(1);
			game.message(player, "drawing " + Card.htmlList(drawn));
			game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
			player.addActions(1);
			game.messageAll("getting +1 action");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "If you've played 3 or more Actions this turn (counting this): +1 Card, +1 Action."};
	}

	@Override
	public String toString() {
		return "Conspirator";
	}
}
