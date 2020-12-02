package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Masquerade extends Card {

	@Override
	public String name() {
		return "Masquerade";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2_Cards>",
				"Each player with any_cards in_hand passes one to the next such player to_their_left, at_once. Then_you may trash a_card from_your_hand."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
        onMasqueradeVariant(player, game, false);
	}

}
