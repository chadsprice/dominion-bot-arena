package cards;

import server.Card;
import server.Game;

public class Talisman extends Card {

	public Talisman() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

	@Override
	public String[] description() {
		return new String[]{"$1", "While this is in play, when you buy a card costing $4 or less that is not a Victory card, gain a copy of it."};
	}

	@Override
	public String toString() {
		return "Talisman";
	}

}
