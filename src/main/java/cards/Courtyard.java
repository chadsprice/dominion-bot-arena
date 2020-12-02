package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Courtyard extends Card {

	@Override
	public String name() {
		return "Courtyard";
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
		return new String[] {
				"<+3_Cards>",
				"Put a_card from your_hand onto your_deck."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 3);
		// put a card from your hand onto your deck
        putACardFromYourHandOntoYourDeck(player, game);
	}

}
