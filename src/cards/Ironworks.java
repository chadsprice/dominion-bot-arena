package cards;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Ironworks extends Card {

	public Ironworks() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// gain a card costing up to $4
		Set<Card> gainable = new HashSet<Card>();
		Card toGain = null;
		for (Map.Entry<Card, Integer> entry : game.supply.entrySet()) {
			Card card = entry.getKey();
			int count = entry.getValue();
			if (card.cost(game) <= 4 && count > 0) {
				gainable.add(card);
			}
		}
		if (gainable.size() == 0) {
			game.message(player, "... You gain nothing");
			game.messageOpponents(player, "... gaining nothing");
			return;
		} else {
			toGain = game.promptChooseGainFromSupply(player, gainable, "Ironworks: Choose a card to gain");
		}
		// gain card
		game.gain(player, toGain);
		game.message(player, "... You gain " + toGain.htmlName());
		game.messageOpponents(player, "... gaining " + toGain.htmlName());
		// +1 action
		if (toGain.isAction) {
			player.addActions(1);
			game.message(player, "... You get +1 action");
			game.messageOpponents(player, "... getting +1 action");
		}
		// +$1
		if (toGain.isTreasure) {
			player.addExtraCoins(1);
			game.message(player, "... You get +$1");
			game.messageOpponents(player, "... getting +$1");
		}
		// +1 card
		if (toGain.isVictory) {
			List<Card> drawn = player.drawIntoHand(1);
			game.message(player, "... You draw " + Card.htmlList(drawn));
			game.messageOpponents(player, "... drawing " + drawn.size() + " card(s)");
		}
	}

	@Override
	public String[] description() {
		return new String[]{"Gain a card costing up to $4.", "If it is an...", "Action card, +1 Action", "Treasure card, +$1", "Victory card, +1 Card"};
	}

	@Override
	public String toString() {
		return "Ironworks";
	}

	@Override
	public String plural() {
		return "Ironworks";
	}

}
