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
	public boolean onPlayWithSelfTrashing(Player player, Game game, boolean hasTrashedSelf) {
		boolean trashesSelf = false;
		if (!hasTrashedSelf) {
			// trash this
			player.removeFromPlay(this);
			game.trash.add(this);
			game.message(player, "... You trash the " + this.htmlNameRaw());
			game.messageOpponents(player, "... trashing the " + this.htmlNameRaw());
			trashesSelf = true;
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
			game.message(player, "... You gain nothing");
			game.messageOpponents(player, "... gaining nothing");
			return trashesSelf;
		} else {
			Card choice = game.promptChooseGainFromSupply(player, gainable, "Feast: Choose a card to gain");
			if (choice != null) {
				toGain = choice;
			} else {
				toGain = gainable.iterator().next();
			}
		}
		// gain card
		game.gain(player, toGain);
		game.message(player, "... You gain " + toGain.htmlName());
		game.messageOpponents(player, "... gaining " + toGain.htmlName());
		return trashesSelf;
	}

	@Override
	public String[] description() {
		return new String[]{"Trash this card.", "Gain a card costing up to $5."};
	}

	public String toString() {
		return "Feast";
	}

}
