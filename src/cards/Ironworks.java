package cards;

import java.util.HashSet;
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
			game.messageAll("gaining nothing");
			return;
		} else {
			toGain = game.promptChooseGainFromSupply(player, gainable, "Ironworks: Choose a card to gain");
		}
		// gain card
		game.gain(player, toGain);
		game.messageAll("gaining " + toGain.htmlName());
		// action -> +1 action
		if (toGain.isAction) {
			plusActions(player, game, 1);
		}
		// treasure -> +$1
		if (toGain.isTreasure) {
			plusCoins(player, game, 1);
		}
		// victory -> +1 card
		if (toGain.isVictory) {
			plusCards(player, game, 1);
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
