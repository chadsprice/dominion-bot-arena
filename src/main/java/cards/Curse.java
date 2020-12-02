package cards;

import server.Card;

import java.util.Collections;
import java.util.Set;

public class Curse extends Card {

	@Override
	public String name() {
		return "Curse";
	}

	@Override
	public Set<Type> types() {
		return Collections.emptySet();
	}

	@Override
	public int cost() {
		return 0;
	}

	@Override
	public String[] description() {
		return new String[]{"-1_VP"};
	}

	@Override
	public int victoryValue() {
		return -1;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 10 * (numPlayers - 1);
	}

}
