package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class City extends Card {

	@Override
	public String name() {
		return "City";
	}

	@Override
	public String plural() {
		return "Cities";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {
				"If there are no empty Supply_piles:",
				"* <+1_Card>, <+2_Actions>",
				"If there is one empty Supply_pile:",
				"* <+2_Cards>, <+2_Actions>",
				"If there are two_or_more:",
				"* <+2_Cards>, <+2_Actions>, <+1$>, <+1_Buy>"
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		int numEmptySupplyPiles = game.numEmptySupplyPiles();
		if (numEmptySupplyPiles == 0) {
			plusCards(player, game, 1);
			plusActions(player, game, 2);
		} else if (numEmptySupplyPiles == 1) {
			plusCards(player, game, 2);
			plusActions(player, game, 2);
		} else {
			plusCards(player, game, 2);
			plusActions(player, game, 2);
			plusCoins(player, game, 1);
			plusBuys(player, game, 1);
		}
	}

}
