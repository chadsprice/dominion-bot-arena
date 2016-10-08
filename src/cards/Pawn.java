package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Pawn extends Card {

	public Pawn() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		List<Integer> benefitIndexes = new ArrayList<Integer>();
		String[] choices = new String[] {"+1 Card", "+1 Action", "+1 Buy", "+$1"};
		int choice = game.promptMultipleChoice(player, "Pawn: Choose the first", choices);
		benefitIndexes.add(choice);
		choice = game.promptMultipleChoice(player, "Pawn: Choose the second", choices, new int[] {choice});
		benefitIndexes.add(choice);
		for (Integer benefit : benefitIndexes) {
			switch (benefit) {
			case 0:
				plusCards(player, game, 1);
				break;
			case 1:
				plusActions(player, game, 1);
				break;
			case 2:
				plusBuys(player, game, 1);
				break;
			default:
				plusCoins(player, game, 1);
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Choose two: +1 Card; +1 Action; +1 Buy; +$1.", "The choices must be different."};
	}

	@Override
	public String toString() {
		return "Pawn";
	}

}
