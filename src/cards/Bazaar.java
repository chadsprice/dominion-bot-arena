package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Bazaar extends Card {

	public Bazaar() {
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
		// +2 actions
		player.addActions(2);
		game.messageAll("getting +2 actions");
		// +$1
		player.addExtraCoins(1);
		game.messageAll("getting +$1");
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+2 Actions", "+$1"};
	}

	@Override
	public String toString() {
		return "Bazaar";
	}

}
