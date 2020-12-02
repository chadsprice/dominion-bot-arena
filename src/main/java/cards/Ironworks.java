package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Ironworks extends Card {

	@Override
	public String name() {
		return "Ironworks";
	}

	@Override
	public String plural() {
		return name();
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[]{
				"Gain a_card costing up_to_4$.",
				"If the gained card is_an...",
				"* Action_card, <+1_Action>",
				"* Treasure_card, <+1$>",
				"* Victory_card, <+1_Card>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		// gain a card costing up to $4
		Card gained = gainCardCostingUpTo(player, game, 4);
		if (gained != null) {
			// action -> +1 action
			if (gained.isAction()) {
				plusActions(player, game, 1);
			}
			// treasure -> +$1
			if (gained.isTreasure()) {
				plusCoins(player, game, 1);
			}
			// victory -> +1 card
			if (gained.isVictory()) {
				plusCards(player, game, 1);
			}
		}
	}

}
