package cards;

import server.Card;

import java.util.Set;

public class Colony extends Card {

	@Override
	public String name() {
		return "Colony";
	}

	@Override
	public String plural() {
		return "Colonies";
	}

	@Override
	public Set<Type> types() {
		return types(Type.VICTORY);
	}

	@Override
	public int cost() {
		return 11;
	}

	@Override
	public String[] description() {
		return new String[]{"10_VP"};
	}

	@Override
	public int victoryValue() {
		return 10;
	}

}
