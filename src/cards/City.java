package cards;

import server.Card;
import server.Game;
import server.Player;

public class City extends Card {

	public City() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		int numEmptySupplyPiles = game.numEmptySupplyPiles();
		if (numEmptySupplyPiles == 0) {
			plusCards(player, game, 1);
			plusActions(player, game, 2);
		} else if (numEmptySupplyPiles == 1) {
			plusCards(player, game, 2);
			plusActions(player, game, 2);
		} else {
			plusCards(player, game, 2);
			plusActions(player, game, 2);
			plusCoins(player, game, 1);
			plusBuys(player, game, 1);
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+2 Actions", "If there are one or more empty Supply piles, +1 Card. If there are two or more, +$1 and +1 Buy."};
	}

	@Override
	public String toString() {
		return "City";
	}

	@Override
	public String plural() {
		return "Cities";
	}

}
