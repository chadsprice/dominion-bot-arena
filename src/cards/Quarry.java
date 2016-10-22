package cards;

import server.Card;
import server.Game;
import server.Player;

public class Quarry extends Card {

	public Quarry() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		game.messageAll("action cards cost $2 less while this is in play");
		game.costModifierPlayedLastTurn = true;
		game.sendCardCosts();
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

	@Override
	public String[] description() {
		return new String[]{"$1", "While this is in play, Action cards cost $2 less, but not less than $0."};
	}

	@Override
	public String toString() {
		return "Quarry";
	}

	@Override
	public String plural() {
		return "Quarries";
	}

}
