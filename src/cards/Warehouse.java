package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Warehouse extends Card {

	public Warehouse() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 3);
		plusActions(player, game, 1);
		// discard 3 cards
		List<Card> toDiscard = game.promptDiscardNumber(player, 3, this.toString());
		for (Card card : toDiscard) {
			player.removeFromHand(card);
			player.addToDiscard(card);
		}
		game.messageAll("discarding " + Card.htmlList(toDiscard));
	}

	@Override
	public String[] description() {
		return new String[] {"+3 Cards", "+1 Action", "Discard 3 cards."};
	}

	@Override
	public String toString() {
		return "Warehouse";
	}

}
