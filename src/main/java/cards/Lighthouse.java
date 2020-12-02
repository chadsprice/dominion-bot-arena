package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Lighthouse extends Card {

	@Override
	public String name() {
		return "Lighthouse";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.DURATION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Action>",
				"Now and at the_start of your next_turn: <+1$>.",
				"While this is in_play, when another_player plays an_Attack card, it doesn't affect_you."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 1);
		plusCoins(player, game, 1);
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		plusCoins(player, game, 1);
	}

}
