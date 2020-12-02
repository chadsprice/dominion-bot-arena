package cards;

import server.Card;

import java.util.Set;

public class Province extends Card {

	@Override
	public String name() {
		return "Province";
	}

	@Override
	public Set<Type> types() {
		return types(Type.VICTORY);
	}

	@Override
	public int cost() {
		return 8;
	}

	@Override
	public String[] description() {
		return new String[]{"6_VP"};
	}

	@Override
	public int victoryValue() {
		return 6;
	}

}
