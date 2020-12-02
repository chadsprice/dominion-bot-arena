package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Cards;

public class Duke extends Card {

	@Override
	public String name() {
		return "Duke";
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
		return new String[] {"Worth 1_VP per_[Duchy] you_have."};
	}

	@Override
	public int victoryValue(List<Card> deck) {
		return (int) deck.stream()
				.filter(c -> c == Cards.DUCHY)
				.count();
	}

}
