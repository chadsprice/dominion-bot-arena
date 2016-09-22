package cards;

import server.Card;
import server.Game;

public class RoyalSeal extends Card {

	public RoyalSeal() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public int treasureValue(Game game) {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[]{"$2", "While this is in play, when you gain a card, you may put that card on top of your deck."};
	}

	@Override
	public String toString() {
		return "Royal Seal";
	}

}
