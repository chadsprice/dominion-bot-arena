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
		plusActions(player, game, 1);
		// discard any number of cards
		List<Card> discarded = game.promptDiscardNumber(player, player.getHand().size(), false, "Cellar");
		player.putFromHandIntoDiscard(discarded);
		// draw the same number of cards
		List<Card> drawn = player.drawIntoHand(discarded.size());
		game.message(player, "discarding " + Card.htmlList(discarded) + " and drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "discarding " + Card.htmlList(discarded) + " and drawing " + Card.numCards(drawn.size()));
	}
	
	@Override
	public String[] description() {
		return new String[]{"+1 Action", "Discard any number of cards, then draw that many."};
	}

	@Override
	public String toString() {
		return "Cellar";
	}

}
