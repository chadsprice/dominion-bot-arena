package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Cellar extends Card {

	@Override
	public String name() {
		return "Cellar";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[]{
				"<+1_Action>",
				"Discard any number of_cards, then_draw that_many."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 1);
		// discard any number of cards
		List<Card> discarded = discardAnyNumber(player, game);
		// draw the same number of cards
		plusCards(player, game, discarded.size());
	}

}
