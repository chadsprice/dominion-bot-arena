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
			// put the chosen card into play
			player.putFromHandIntoPlay(toPlay);
			game.message(player, "... You choose " + toPlay.htmlName());
			game.messageOpponents(player, "...  choosing " + toPlay.htmlName());
			// remember if the card trashes itself on the first play
			// necessary for cards like mining village
			boolean hasTrashedSelf = game.playAction(player, toPlay, false);
			if (toPlay.isDuration) {
				player.setDurationModifier(this);
			}
			game.playAction(player, toPlay, hasTrashedSelf);
		} else {
			game.message(player, "... You have no actions");
			game.messageOpponents(player, " ... having no actions");
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
