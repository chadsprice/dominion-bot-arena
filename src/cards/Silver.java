package cards;

import server.Card;

public class Silver extends Card {

	public Silver() {
		isTreasure = true;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 40;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public int treasureValue() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[]{"$2"};
	}

	@Override
	public String toString() {
		return "Silver";
	}

}
