package cards;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Feast extends Card {

	public Feast() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		boolean movedToTrash = false;
		if (!hasMoved) {
			// trash this
			player.removeFromPlay(this);
			game.trash.add(this);
			game.messageAll("trashing the " + this.htmlNameRaw());
			movedToTrash = true;
		}
		// gain a card costing up to $5
		Set<Card> gainable = new HashSet<Card>();
		Card toGain = null;
		for (Map.Entry<Card, Integer> entry : game.supply.entrySet()) {
			Card card = entry.getKey();
			int count = entry.getValue();
			if (card.cost() <= 5 && count > 0) {
				gainable.add(card);
			}
		}
		if (gainable.size() == 0) {
			game.messageAll("gaining nothing");
			return movedToTrash;
		} else {
			Card choice = game.promptChooseGainFromSupply(player, gainable, "Feast: Choose a card to gain");
			if (choice != null) {
				toGain = choice;
			} else {
				toGain = gainable.iterator().next();
			}
		}
		// gain card
		game.messageAll("gaining " + toGain.htmlName());
		game.gain(player, toGain);
		return movedToTrash;
	}

	@Override
	public String[] description() {
		return new String[]{"Trash this card.", "Gain a card costing up to $5."};
	}

	public String toString() {
		return "Feast";
	}

}
