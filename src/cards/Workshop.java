package cards;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Workshop extends Card {

	public Workshop() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
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
			toGain = game.promptChooseGainFromSupply(player, gainable, "Workshop: Choose a card to gain");
		}
		// gain card
		game.messageAll("gaining " + toGain.htmlName());
		game.gain(player, toGain);
	}

	@Override
	public String[] description() {
		return new String[]{"Gain a card costing up to $4."};
	}

	@Override
	public String toString() {
		return "Workshop";
	}

}
