package cards;

import java.util.List;

import server.Card;

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
		int numDuchies = 0;
		for (Card card : deck) {
			if (card == Card.DUCHY) {
				numDuchies++;
			}
		}
		return numDuchies;
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
