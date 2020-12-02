package cards;

import server.Card;

import java.util.Set;

public class Estate extends Card {

	@Override
	public String name() {
		return "Estate";
	}

	@Override
	public Set<Type> types() {
		return types(Type.VICTORY);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[]{"1_VP"};
	}

	@Override
	public int victoryValue() {
		return 1;
	}

}
