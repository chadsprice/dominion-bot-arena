package cards;

import server.Card;
import server.Game;

import java.util.Set;

public class Bank extends Card {

	@Override
	public String name() {
		return "Bank";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
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
		return new String[]{"When you play_this, it's_worth 1$ per_Treasure_card you_have in_play (counting_this)."};
	}

}
