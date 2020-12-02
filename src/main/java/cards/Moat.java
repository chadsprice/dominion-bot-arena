package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Moat extends Card {

	@Override
	public String name() {
		return "Moat";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK_REACTION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[]{
				"<+2_Cards>",
				"When another player plays an_Attack card, you_may first reveal this from your_hand, to_be unaffected_by_it."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
	}

	@Override
	public boolean onAttackReaction(Player player, Game game) {
		return true;
	}

}
