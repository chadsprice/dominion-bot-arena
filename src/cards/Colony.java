package cards;

import server.Card;

public class Colony extends Card {

	public Colony() {
		isVictory = true;
	}

	@Override
	public int cost() {
		return 11;
	}

	@Override
	public int victoryValue() {
		return 10;
	}

	@Override
	public String[] description() {
		return new String[]{"10 VP"};
	}

	@Override
	public String toString() {
		return "Colony";
	}

	@Override
	public String plural() {
		return "Colonies";
	}

}
