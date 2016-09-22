package cards;

import server.Card;

public class Platinum extends Card {

	public Platinum() {
		isTreasure = true;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 12;
	}

	@Override
	public int cost() {
		return 9;
	}

	@Override
	public int treasureValue() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{"$5"};
	}

	@Override
	public String toString() {
		return "Platinum";
	}

}
