package cards;

import server.Card;

public class Estate extends Card {

	public Estate() {
		isVictory = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public int victoryValue() {
		return 1;
	}

	@Override
	public String[] description() {
		return new String[]{"1 VP"};
	}

	@Override
	public String toString() {
		return "Estate";
	}

}
