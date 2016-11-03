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
		List<Card> discarded = discardAnyNumber(player, game);
		// draw the same number of cards
		plusCards(player, game, discarded.size());
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
