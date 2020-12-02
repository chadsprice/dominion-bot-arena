package cards;

import server.Card;
import server.Game;

import java.util.Set;

public class Hoard extends Card {

	@Override
	public String name() {
		return "Hoard";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[]{
				"2$",
				"While this is in_play, when_you buy a_Victory card, gain a_[Gold]."
		};
	}

	@Override
	public int treasureValue(Game game) {
		return 2;
	}

}
