package cards;

import server.Card;

public class Province extends Card {

	public Province() {
		isVictory = true;
	}

	@Override
	public int cost() {
		return 8;
	}

	@Override
	public int victoryValue() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[]{"6 VP"};
	}

	@Override
	public String toString() {
		return "Province";
	}

}
