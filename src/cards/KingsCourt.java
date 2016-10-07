package cards;

import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class KingsCourt extends Card {

	public KingsCourt() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 7;
	}

	@Override
	public void onPlay(Player player, Game game) {
		Set<Card> actions = game.playableActions(player);
		if (!actions.isEmpty()) {
			Card toPlay = game.promptChoosePlay(player, actions, "King's Court: Choose an action to play three times", false, "None");
			if (toPlay != null) {
				// put the chosen card into play
				player.putFromHandIntoPlay(toPlay);
				game.messageAll("choosing " + toPlay.htmlName());
				// remember if the card moves itself
				// necessary for the "lose track" rule
				boolean hasMoved = false;
				for (int i = 0; i < 3; i++) {
					hasMoved |= game.playAction(player, toPlay, hasMoved);
					if (toPlay.isDuration && hasMoved) {
						player.setDurationModifier(this);
					}
				}
			} else {
				game.messageAll("choosing nothing");
			}
		} else {
			game.messageAll("having no actions");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"You may choose an action card in your hand. Play it three times."};
	}

	@Override
	public String toString() {
		return "King's Court";
	}

}
