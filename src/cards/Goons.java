package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Goons extends Card {

	public Goons() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
		// each other player discards down to 3 cards in hand
		handSizeAttack(targets, game, 3);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "+$2", "Each other player discards down to 3 cards in hand.", "While this is in play, when you buy a card, +1 VP."};
	}

	@Override
	public String toString() {
		return "Goons";
	}

}
