package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class CouncilRoom extends Card {

	public CouncilRoom() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +4 cards
		List<Card> drawn = player.drawIntoHand(4);
		// +1 buys
		player.addBuys(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 buy");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 buy");
		// each other player draws a card
		for (Player opponent : game.getOpponents(player)) {
			drawn = opponent.drawIntoHand(1);
			game.message(opponent, "... You draw " + Card.htmlList(drawn));
			game.messageOpponents(opponent, "... " + opponent.username + " draws " + drawn.size() + " card(s)");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+4 Cards", "+1 Buy", "Each other player draws a card."};
	}

	@Override
	public String toString() {
		return "Council Room";
	}

}
