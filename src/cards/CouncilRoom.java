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
		plusCards(player, game, 4);
		plusBuys(player, game, 1);
		// each other player draws a card
		for (Player opponent : game.getOpponents(player)) {
			List<Card> drawn = opponent.drawIntoHand(1);
			game.message(opponent, "You draw " + Card.htmlList(drawn));
			game.messageOpponents(opponent, opponent.username + " draws " + Card.numCards(drawn.size()));
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
