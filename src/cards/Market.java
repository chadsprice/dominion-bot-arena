package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Market extends Card {

	public Market() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// +1 action
		player.addActions(1);
		game.messageAll("getting +1 action");
		// +1 buy
		player.addBuys(1);
		game.messageAll("getting +1 buy");
		// +$1
		player.addExtraCoins(1);
		game.messageAll("getting +$1");
	}

	@Override
	public String[] description() {
		return new String[]{"+1 Card", "+1 Action", "+1 Buy", "+$1"};
	}

	@Override
	public String toString() {
		return "Market";
	}

}
