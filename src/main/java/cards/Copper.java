package cards;

import server.Card;
import server.Game;

import java.util.Set;

public class Copper extends Card {

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public String name() {
		return "Copper";
	}

	@Override
	public int cost() {
		return 0;
	}

	@Override
	public String[] description() {
		return new String[]{"1$"};
	}

	@Override
	public int treasureValue(Game game) {
		return 1 + game.coppersmithsPlayedThisTurn;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 60 - 7 * numPlayers;
	}

}
