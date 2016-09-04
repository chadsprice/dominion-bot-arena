package cards;

import server.Card;

public class Duchy extends Card {

	public Duchy() {
		isVictory = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public int victoryValue() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[]{"3 VP"};
	}

	@Override
	public String toString() {
		return "Duchy";
	}

	@Override
	public String plural() {
		return "Duchies";
	}

}
