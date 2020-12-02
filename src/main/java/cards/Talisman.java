package cards;

import server.Card;
import server.Game;

import java.util.Set;

public class Talisman extends Card {

	@Override
	public String name() {
		return "Talisman";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 4;
	}

    @Override
    public String[] description() {
        return new String[]{
                "1$",
                "While this is in_play, when_you buy a_card costing 4$_or_less that is_not a_Victory card, gain a_copy of_it."
        };
    }

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

}
