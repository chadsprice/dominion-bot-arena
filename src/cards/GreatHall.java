package cards;

import java.util.List;

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
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		// +1 action
		player.addActions(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 action");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 action");
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
