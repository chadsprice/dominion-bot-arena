package cards;

import server.Card;

public class Harem extends Card {

	public Harem() {
		isTreasure = true;
		isVictory = true;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public int treasureValue() {
		return 2;
	}

	@Override
	public int victoryValue() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[] {"$2", "2 VP"};
	}

	@Override
	public String toString() {
		return "Harem";
	}

}
