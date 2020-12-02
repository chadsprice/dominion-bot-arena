package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Conspirator extends Card {

	@Override
	public String name() {
		return "Conspirator";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+$2>",
				"If you've played 3_or_more Actions this_turn (counting_this), <+1_Card> and <+1_Action>."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
		// if you played at least 3 actions this turn, +1 card, +1 action
		if (game.actionsPlayedThisTurn >= 3) {
			plusCards(player, game, 1);
			plusActions(player, game, 1);
		}
	}

}
