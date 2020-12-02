package cards;

import server.Card;

import java.util.Set;

public class Harem extends Card {

	@Override
	public String name() {
		return "Harem";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE, Type.VICTORY);
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[] {
				"$2",
				"2_VP"
		};
	}

	@Override
	public int treasureValue() {
		return 2;
	}

	@Override
	public int victoryValue() {
		return 2;
	}

}
