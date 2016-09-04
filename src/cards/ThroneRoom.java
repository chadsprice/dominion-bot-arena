package cards;

import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class ThroneRoom extends Card {

	public ThroneRoom() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		Set<Card> actions = game.playableActions(player);
		if (actions.size() > 0) {
			Card toPlay = game.promptChoosePlay(player, actions, "Throne Room: Choose an action to play twice");
			if (toPlay == null) {
				toPlay = actions.iterator().next();
			}
			// put the chosen card into play
			player.putFromHandIntoPlay(toPlay);
			game.message(player, "... You play " + toPlay.htmlName() + " twice");
			game.messageOpponents(player, "...  playing " + toPlay.htmlName() + " twice");
			// this may not be a sufficient check to ensure that one-shots cannot be throne room'd
			int initialCount = 0;
			for (Card card : player.getPlay()) {
				if (card == toPlay) {
					initialCount++;
				}
			}
			game.playAction(player, toPlay);
			int finalCount = 0;
			for (Card card : player.getPlay()) {
				if (card == toPlay) {
					finalCount++;
				}
			}
			if (initialCount <= finalCount) {
				game.playAction(player, toPlay);
			} else {
				game.message(player, toPlay.htmlNameRaw() + " cannot be played again because it has been trashed");
				game.messageOpponents(player, toPlay.htmlNameRaw() + " cannot be played again because it has been trashed");
			}
		} else {
			game.message(player, "... You have no actions to play twice");
			game.messageOpponents(player, " ... having no actions to play twice");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Choose an action card in your hand.", "Play it twice."};
	}

	@Override
	public String toString() {
		return "Throne Room";
	}

}
