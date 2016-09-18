package cards;

import server.Card;
import server.Game;
import server.Player;

public class Bazaar extends Card {

	public Bazaar() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 2);
		plusCoins(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+2 Actions", "+$1"};
	}

	@Override
	public String toString() {
		return "Bazaar";
	}

}
