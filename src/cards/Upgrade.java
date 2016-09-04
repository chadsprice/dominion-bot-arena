package cards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Upgrade extends Card {

	public Upgrade() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		// +1 action
		player.addActions(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 action");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 action");
		// trash a card from your hand
		if (player.getHand().size() > 0) {
			Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Upgrade: Choose a card to trash from your hand");
			player.removeFromHand(toTrash);
			game.trash.add(toTrash);
			game.message(player, "... You trash " + toTrash.htmlName());
			game.messageOpponents(player, "... trashing " + toTrash.htmlName());
			Set<Card> gainable = game.cardsCostingExactly(toTrash.cost(game) + 1);
			// if there are cards that can be gained
			if (gainable.size() > 0) {
				Card toGain = game.promptChooseGainFromSupply(player, gainable, "Upgrade: Choose a card to gain");
				game.gain(player, toGain);
				game.message(player, "... You gain " + toGain.htmlName());
				game.messageOpponents(player, "... gaining " + toGain.htmlName());
			} else {
				game.message(player, "... You gain nothing");
				game.messageOpponents(player, "... gaining nothing");
			}
		} else {
			game.message(player, "... Your hand is empty");
			game.messageOpponents(player, "... " + player.username + "'s hand is empty");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Trash a card from your hand.", "Gain a card costing exactly $1 more than it."};
	}

	@Override
	public String toString() {
		return "Upgrade";
	}

}
