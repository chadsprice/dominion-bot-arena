package cards;

import server.Card;

import java.util.Set;

public class Platinum extends Card {

	@Override
	public String name() {
		return "Platinum";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
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
	public String[] description() {
		return new String[]{"5$"};
	}

	@Override
	public int treasureValue() {
		return 5;
	}

}
