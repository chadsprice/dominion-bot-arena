package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Outpost extends Card {

	@Override
	public String name() {
		return "Outpost";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.DURATION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {
				"You only draw 3_cards (instead_of_5) in this turn's Clean-up_phase.",
				"Take an_extra_turn after this_one.",
				"This can't cause you to take more_than two consecutive_turns."
		};
	}

	@Override
	public boolean onDurationPlay(Player player, Game game, List<Card> havened) {
		if (!player.isTakingExtraTurn()) {
			if (!player.hasExtraTurn()) {
				game.messageAll("getting an extra 3 card turn after this one");
				return true;
			} else {
				game.messageAll("but will still only get one extra turn");
			}
		} else {
			// playing an outpost during an extra turn
			game.messageAll("but can't take more than two consecutive turns");
		}
		return false;
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		// outpost has no duration effect beyond the extra turn
	}

}
