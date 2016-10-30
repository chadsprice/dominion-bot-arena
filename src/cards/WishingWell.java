package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class WishingWell extends Card {

	public WishingWell() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		tryToNameTopCardOfDeck(player, game, "Wishing Well");
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Name a card, then reveal the top card of your deck. If you named it, put it into your hand."};
	}

	@Override
	public String toString() {
		return "Wishing Well";
	}

}
