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
		// if you played at least 3 actions this turn, +1 card, +1 action
		if (game.actionsPlayedThisTurn >= 3) {
			List<Card> drawn = player.drawIntoHand(1);
			player.addActions(1);
			game.message(player, "... You get +$2, draw " + Card.htmlList(drawn) + ", and get +1 action");
			game.messageOpponents(player, "... getting +$2, drawing " + drawn.size() + " card(s), and getting +1 action");
		} else {
			game.message(player, "... You get +$2");
			game.messageOpponents(player, "... getting +$2");
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
