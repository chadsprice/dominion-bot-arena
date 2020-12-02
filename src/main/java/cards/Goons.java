package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Goons extends Card {

	@Override
	public String name() {
		return "Goons";
	}

	@Override
	public String plural() {
		return name();
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK);
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Buy>",
				"<+2$>",
				"Each other player discards down_to 3_cards in_hand.", "While this is in_play, when you buy a_card, <+1_VP>."
		};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
		// each other player discards down to 3 cards in hand
		handSizeAttack(targets, game, 3);
	}

}
