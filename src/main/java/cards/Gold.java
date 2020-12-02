package cards;

import server.Card;

import java.util.Set;

public class Gold extends Card {

	@Override
	public String name() {
		return "Gold";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[]{"3$"};
	}

	@Override
	public int treasureValue() {
		return 3;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 30;
	}

}
