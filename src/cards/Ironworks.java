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
		Set<Card> gainable = game.cardsCostingAtMost(4);
		if (!gainable.isEmpty()) {
			Card toGain = game.promptChooseGainFromSupply(player, gainable, "Ironworks: Choose a card to gain.");
			game.messageAll("gaining " + toGain.htmlName());
			game.gain(player, toGain);
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
		} else {
			game.messageAll("gaining nothing");
		}
	}

	@Override
	public String[] description() {
		return new String[]{"Gain a card costing up to $4.", "If the gained card is an...", "Action card, +1 Action", "Treasure card, +$1", "Victory card, +1 Card"};
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
