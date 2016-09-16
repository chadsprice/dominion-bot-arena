package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Nobles extends Card {

	public Nobles() {
		isAction = true;
		isVictory = true;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public int victoryValue() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		int choice = game.promptMultipleChoice(player, "Nobles: Choose one", new String[] {"+3 Cards", "+2 Actions"});
		if (choice == 0) {
			// +3 cards
			List<Card> drawn = player.drawIntoHand(3);
			game.message(player, "drawing " + Card.htmlList(drawn));
			game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		} else {
			// +2 Actions
			player.addActions(2);
			game.messageAll("getting +2 actions");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Choose one: +3 Cards; or +2 Actions.", "2 VP"};
	}

	@Override
	public String toString() {
		return "Nobles";
	}

	@Override
	public String plural() {
		return "Nobles";
	}

}
