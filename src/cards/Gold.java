package cards;

import server.Card;

public class Gold extends Card {

	public Gold() {
		isTreasure = true;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 30;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public int treasureValue() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[]{"$3"};
	}

	@Override
	public String toString() {
		return "Gold";
	}

}
