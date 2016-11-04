package cards;

import server.Card;
import server.Game;
import server.Player;

public class Courtyard extends Card {

	public Courtyard() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 3);
		// put a card from your hand onto your deck
        putACardFromYourHandOntoYourDeck(player, game);
	}

	@Override
	public String[] description() {
		return new String[] {"+3 Cards", "Put a card from your hand onto your deck."};
	}

	public String toString() {
		return "Courtyard";
	}

}
