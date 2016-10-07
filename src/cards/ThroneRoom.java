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
		if (!actions.isEmpty()) {
			// you may choose a card to play twice
			Card toPlay = game.promptChoosePlay(player, actions, "Throne Room: Choose an action to play twice", false, "None");
			if (toPlay != null) {
				// put the chosen card into play
				player.putFromHandIntoPlay(toPlay);
				game.messageAll("choosing " + toPlay.htmlName());
				// remember if the card moves itself
				// necessary for the "lose track" rule
				boolean hasMoved = game.playAction(player, toPlay, false);
				if (toPlay.isDuration && hasMoved) {
					player.setDurationModifier(this);
				}
				game.playAction(player, toPlay, hasMoved);
			} else {
				game.messageAll("choosing nothing");
			}
		} else {
			game.messageAll("having no actions");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"You may play an Action card from your hand twice."};
	}

	@Override
	public String toString() {
		return "Throne Room";
	}

}
