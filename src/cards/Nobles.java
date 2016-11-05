package cards;

import server.Bot;
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
		if (chooseCardsOverActions(player, game)) {
			plusCards(player, game, 3);
		} else {
			plusActions(player, game, 2);
		}
	}

	private boolean chooseCardsOverActions(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).noblesCardsOverActions();
		}
		int choice = game.promptMultipleChoice(player, this.toString() + ": Choose one", new String[] {"+3 Cards", "+2 Actions"});
		return (choice == 0);
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
