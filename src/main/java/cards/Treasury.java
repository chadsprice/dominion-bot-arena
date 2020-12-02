package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Treasury extends Card {

	@Override
	public String name() {
		return "Treasury";
	}

	@Override
	public String plural() {
		return "Treasuries";
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
				"<+1_Card>",
				"<+1_Action>",
				"<+1$>",
				"When you discard this from_play, if_you didn't buy a_Victory card this_turn, you_may put_this on_top of your_deck."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		plusCoins(player, game, 1);
	}

}
