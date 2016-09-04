package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Laboratory extends Card {

	public Laboratory() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		// +1 action
		player.addActions(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 action");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 action");
	}

	@Override
	public String[] description() {
		return new String[]{"+2 Cards", "+1 Action"};
	}

	@Override
	public String toString() {
		return "Laboratory";
	}

	@Override
	public String plural() {
		return "Laboratories";
	}

}
