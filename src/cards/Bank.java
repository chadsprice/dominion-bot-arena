package cards;

import server.Card;
import server.Game;

public class Bank extends Card {

	public Bank() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 7;
	}

	@Override
	public int treasureValue(Game game) {
		return game.numTreasuresInPlay();
	}

	@Override
	public String[] description() {
		return new String[]{"When you play this, it's worth $1 per Treasure card you have in play (counting this)."};
	}

	@Override
	public String toString() {
		return "Bank";
	}

}
