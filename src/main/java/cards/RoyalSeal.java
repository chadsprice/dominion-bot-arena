package cards;

import server.Card;
import server.Game;

import java.util.Set;

public class RoyalSeal extends Card {

	@Override
	public String name() {
		return "Royal Seal";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{
				"$2",
				"While this is in_play, when you gain a_card, you_may put that_card on_top of your_deck."
		};
	}

	@Override
	public int treasureValue(Game game) {
		return 2;
	}

}
