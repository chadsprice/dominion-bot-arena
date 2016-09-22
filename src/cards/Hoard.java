package cards;

import server.Card;
import server.Game;

public class Hoard extends Card {

	public Hoard() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public int treasureValue(Game game) {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[]{"$2", "While this is in play, when you buy a Victory card, gain a Gold."};
	}

	@Override
	public String toString() {
		return "Hoard";
	}

}
