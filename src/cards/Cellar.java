package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Cellar extends Card {

	public Cellar() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 action
		player.addActions(1);
		game.message(player, "... You get +1 action");
		game.messageOpponents(player, "... getting +1 action");
		// discard any number of cards
		List<Card> discarded = game.promptDiscardNumber(player, player.getHand().size(), false, "Cellar", "discard");
		player.putFromHandIntoDiscard(discarded);
		// draw the same number of cards
		List<Card> drawn = player.drawIntoHand(discarded.size());
		game.message(player, "... You discard " + Card.htmlList(discarded) + " and draw " + Card.htmlList(drawn));
		game.messageOpponents(player, "... discarding " + Card.htmlList(discarded) + " and drawing " + drawn.size() + " card(s)");
	}
	
	@Override
	public String[] description() {
		return new String[]{"+1 action", "Discard any number of cards.", "+1 Card per card discarded."};
	}

	@Override
	public String toString() {
		return "Cellar";
	}

}
