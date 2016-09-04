package cards;

import server.Card;

public class Curse extends Card {

	public Curse() {
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 10 * (numPlayers - 1);
	}

	@Override
	public int cost() {
		return 0;
	}

	@Override
	public int victoryValue() {
		return -1;
	}

	@Override
	public String[] description() {
		return new String[]{"-1 VP"};
	}

	@Override
	public String toString() {
		return "Curse";
	}

}
