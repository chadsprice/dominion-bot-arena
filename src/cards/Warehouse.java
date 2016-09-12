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
		// +3 cards
		List<Card> drawn = player.drawIntoHand(3);
		// +1 actions
		player.addActions(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 action");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 action");
		// discard 3 cards
		List<Card> toDiscard = game.promptDiscardNumber(player, 3, this.toString(), "actionPrompt");
		for (Card card : toDiscard) {
			player.removeFromHand(card);
			player.addToDiscard(card);
		}
		game.message(player, "... You discard " + Card.htmlList(toDiscard));
		game.messageOpponents(player, "... discarding " + toDiscard.size() + " card(s)");
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
