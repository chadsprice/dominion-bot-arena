package cards;

import java.util.HashSet;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Expand extends Card {

	public Expand() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 7;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// trash a card from hand
		if (!player.getHand().isEmpty()) {
			Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Expand: Choose a card to trash");
			// trash card
			game.messageAll("trashing " + toTrash.htmlName());
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
			// gain a card costing up to 3 more
			Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 3);
			if (!gainable.isEmpty()) {
				Card toGain = game.promptChooseGainFromSupply(player, gainable, "Expand: Choose a card to gain");
				game.messageAll("gaining " + toGain.htmlName());
				game.gain(player, toGain);
			} else {
				game.messageAll("gaining nothing");
			}
		} else {
			game.messageAll("having no card to trash");
		}
	}

	@Override
	public String[] description() {
		return new String[]{"Trash a card from your hand.", "Gain a card costing up to $3 more than the trashed card."};
	}

	@Override
	public String toString() {
		return "Expand";
	}

}
