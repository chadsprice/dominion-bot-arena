package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Outpost extends Card {

	public Outpost() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 5;
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

	@Override
	public String[] description() {
		return new String[] {"You only draw 3 cards (instead of 5) in this turn's Clean-up phase.", "Take an extra turn after this one.", "This can't cause you to take more than two consecutive turns."};
	}

	@Override
	public String toString() {
		return "Outpost";
	}

}
