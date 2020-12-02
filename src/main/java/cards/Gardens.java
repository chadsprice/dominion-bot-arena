package cards;

import java.util.List;
import java.util.Set;

import server.Card;

public class Gardens extends Card {

	@Override
	public String name() {
		return "Gardens";
	}

	@Override
	public String plural() {
		return name();
	}

	@Override
	public Set<Type> types() {
		return types(Type.VICTORY);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {"Worth 1_VP per_10_cards you_have (rounded_down)."};
	}

	@Override
	public int victoryValue(List<Card> deck) {
		return deck.size() / 10;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return numPlayers == 2 ? 8 : 12;
	}

}
