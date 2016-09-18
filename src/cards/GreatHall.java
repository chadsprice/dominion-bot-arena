package cards;

import server.Card;
import server.Game;
import server.Player;

public class GreatHall extends Card {

	public GreatHall() {
		isAction = true;
		isVictory = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public int victoryValue() {
		return 1;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "1 VP"};
	}

	@Override
	public String toString() {
		return "Great Hall";
	}

}
