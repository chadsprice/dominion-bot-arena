package cards;

import server.Card;

import java.util.Set;

public class Duchy extends Card {

	@Override
	public String name() {
		return "Duchy";
	}

	@Override
	public String plural() {
		return "Duchies";
	}

	@Override
	public Set<Type> types() {
		return types(Type.VICTORY);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{"3_VP"};
	}

	@Override
	public int victoryValue() {
		return 3;
	}

}
