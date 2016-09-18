package cards;

import java.util.HashSet;
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
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		// trash a card from your hand
		if (player.getHand().size() > 0) {
			Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Upgrade: Choose a card to trash from your hand");
			player.removeFromHand(toTrash);
			game.trash.add(toTrash);
			game.messageAll("trashing " + toTrash.htmlName());
			Set<Card> gainable = game.cardsCostingExactly(toTrash.cost(game) + 1);
			// if there are cards that can be gained
			if (gainable.size() > 0) {
				Card toGain = game.promptChooseGainFromSupply(player, gainable, "Upgrade: Choose a card to gain");
				game.gain(player, toGain);
				game.messageAll("gaining " + toGain.htmlName());
			} else {
				game.messageAll("gaining nothing");
			}
		} else {
			game.messageAll("having no card to trash");
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
