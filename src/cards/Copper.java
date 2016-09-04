package cards;

import server.Card;
import server.Game;

public class Copper extends Card {

	public Copper() {
		isTreasure = true;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 60 - 7 * numPlayers;
	}

	@Override
	public int cost() {
		return 0;
	}

	@Override
	public int treasureValue(Game game) {
		return 1 + game.coppersmithsPlayedThisTurn;
	}

	@Override
	public String[] description() {
		return new String[]{"$1"};
	}

	@Override
	public String toString() {
		return "Copper";
	}

}
