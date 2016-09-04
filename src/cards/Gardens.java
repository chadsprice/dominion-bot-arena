package cards;

import java.util.List;

import server.Card;

public class Gardens extends Card {

	public Gardens() {
		isVictory = true;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return numPlayers == 2 ? 8 : 12;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public int victoryValue(List<Card> deck) {
		return deck.size() / 10;
	}

	@Override
	public String[] description() {
		return new String[] {"Worth 1 VP for every 10 cards in your deck (rounded down)."};
	}

	@Override
	public String toString() {
		return "Gardens";
	}

	@Override
	public String plural() {
		return "Gardens";
	}

}
