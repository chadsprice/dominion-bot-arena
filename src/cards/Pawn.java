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
				// +1 card
				List<Card> drawn = player.drawIntoHand(1);
				game.message(player, "drawing " + Card.htmlList(drawn));
				game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
				break;
			case 1:
				// +1 action
				player.addActions(1);
				game.messageAll("getting +1 action");
				break;
			case 2:
				// +1 buy
				player.addBuys(1);
				game.messageAll("getting +1 buy");
				break;
			default:
				// +$1
				player.addExtraCoins(1);
				game.messageAll("getting +$1");
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Choose two; +1 Card; +1 Action; +1 Buy; +$1.", "(The choices must be different.)"};
	}

	@Override
	public String toString() {
		return "Pawn";
	}

}
