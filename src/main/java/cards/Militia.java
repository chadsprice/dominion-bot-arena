package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Militia extends Card {

	@Override
	public String name() {
		return "Militia";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK);
	}
	
	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[]{
				"<+2$>",
				"Each other_player discards down_to 3_cards in_hand."
		};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCoins(player, game, 2);
		// each other player discards down to 3
		handSizeAttack(targets, game, 3);
	}
	
}
