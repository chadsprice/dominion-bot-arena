package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Warehouse extends Card {

	@Override
	public String name() {
		return "Warehouse";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+3_Cards>",
				"<+1_Action>",
				"Discard 3 cards."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 3);
		plusActions(player, game, 1);
		// discard 3 cards
		List<Card> toDiscard = promptDiscardNumber(player, game, 3);
		game.messageAll("discarding " + Card.htmlList(toDiscard));
		player.putFromHandIntoDiscard(toDiscard);
	}

}
