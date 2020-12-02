package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class WishingWell extends Card {

	@Override
	public String name() {
		return "Wishing Well";
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
				"<+1_Card>",
				"<+1_Action>",
				"Name a card, then reveal the top card of your_deck. If_you named_it, put_it into your_hand."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		tryToNameTopCardOfDeck(player, game);
	}

}
