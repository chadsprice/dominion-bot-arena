package cards;

import java.util.List;

import server.Card;
import server.Cards;

public class Duke extends Card {

	public Duke() {
		isVictory = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public int victoryValue(List<Card> deck) {
		return (int) deck.stream()
				.filter(c -> c == Cards.DUCHY)
				.count();
	}

	@Override
	public String[] description() {
		return new String[] {"Worth 1 VP per Duchy you have."};
	}

	@Override
	public String toString() {
		return "Duke";
	}

}
