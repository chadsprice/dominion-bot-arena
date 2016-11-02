package cards;

import java.util.Set;
import java.util.stream.Collectors;

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
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		boolean usedAsModifier = false;
		Set<Card> actions = player.getHand().stream().filter(c -> c.isAction).collect(Collectors.toSet());
		if (!actions.isEmpty()) {
			Card toPlay = game.promptChoosePlay(player, actions, "King's Court: You may play an action from you hand three times.", false, "None");
			if (toPlay != null) {
				game.messageAll("choosing " + toPlay.htmlName());
				// put the chosen card into play
				player.putFromHandIntoPlay(toPlay);
				// play it twice
				boolean toPlayMoved = false;
				for (int i = 0; i < 3; i++) {
					toPlayMoved |= game.playAction(player, toPlay, toPlayMoved);
					// if the card was a duration card, and it was set aside, and this hasn't been moved
					if (toPlay.isDuration && toPlayMoved && !hasMoved && !usedAsModifier) {
						// set this aside as a modifier
                        player.removeFromPlay(this);
						player.addDurationSetAside(this);
						usedAsModifier = true;
					}
				}
			} else {
				game.messageAll("choosing nothing");
			}
		} else {
			game.messageAll("having no actions");
		}
		return usedAsModifier;
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
