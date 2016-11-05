package cards;

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
		Card gained = gainCardCostingUpTo(player, game, 4);
		if (gained != null) {
			// action -> +1 action
			if (gained.isAction) {
				plusActions(player, game, 1);
			}
			// treasure -> +$1
			if (gained.isTreasure) {
				plusCoins(player, game, 1);
			}
			// victory -> +1 card
			if (gained.isVictory) {
				plusCards(player, game, 1);
			}
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
