package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Village extends Card {

	public Village() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
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
	}

	@Override
	public String[] description() {
		return new String[]{"+1 Card", "+2 Actions"};
	}

	@Override
	public String toString() {
		return "Village";
	}

}
