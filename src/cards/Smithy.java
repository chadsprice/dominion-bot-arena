package cards;

import server.Card;
import server.Game;
import server.Player;

public class Smithy extends Card {

	public Smithy() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 3);
	}

	@Override
	public String[] description() {
		return new String[]{"+3 Cards"};
	}

	@Override
	public String toString() {
		return "Smithy";
	}

	@Override
	public String plural() {
		return "Smithies";
	}

}
