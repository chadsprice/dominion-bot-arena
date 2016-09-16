package cards;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Remodel extends Card {

	public Remodel() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// trash a card from hand
		Set<Card> trashable = new HashSet<Card>(player.getHand());
		Card toTrash = null;
		if (trashable.size() == 0) {
			game.messageAll("trashing nothing");
			return;
		} else {
			Card choice = game.promptChooseTrashFromHand(player, trashable, "Remodel: Choose a card to trash");
			if (choice != null) {
				toTrash = choice;
			} else {
				toTrash = trashable.iterator().next();
			}
		}
		int cost = toTrash.cost(game);
		// trash card
		player.removeFromHand(toTrash);
		game.trash.add(toTrash);
		game.messageAll("trashing " + toTrash.htmlName());

		// gain a card costing up to 2 more
		int maxCost = cost + 2;
		Set<Card> gainable = new HashSet<Card>();
		Card toGain = null;
		for (Map.Entry<Card, Integer> entry : game.supply.entrySet()) {
			Card card = entry.getKey();
			int count = entry.getValue();
			if (card.cost(game) <= maxCost && count > 0) {
				gainable.add(card);
			}
		}
		if (gainable.size() == 0) {
			game.messageAll("gaining nothing");
			return;
		} else {
			Card choice = game.promptChooseGainFromSupply(player, gainable, "Remodel: Choose a card to gain");
			if (choice != null) {
				toGain = choice;
			} else {
				toGain = gainable.iterator().next();
			}
		}
		// gain card
		game.gain(player, toGain);
		game.messageAll("gaining " + toGain.htmlName());
	}

	@Override
	public String[] description() {
		return new String[]{"Trash a card from your hand.", "Gain a card costing up to $2 more than the trashed card."};
	}

	@Override
	public String toString() {
		return "Remodel";
	}

}
