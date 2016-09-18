package cards;

import server.Card;
import server.Game;
import server.Player;

public class Laboratory extends Card {

	public Laboratory() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
		plusActions(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[]{"+2 Cards", "+1 Action"};
	}

	@Override
	public String toString() {
		return "Laboratory";
	}

	@Override
	public String plural() {
		return "Laboratories";
	}

}
