package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class CouncilRoom extends Card {

	@Override
	public String name() {
		return "Council Room";
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
				"<+4_Cards>",
				"<+1_Buy>",
				"Each other_player draws_a_card."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 4);
		plusBuys(player, game, 1);
		// each other player draws a card
		for (Player opponent : game.getOpponents(player)) {
			List<Card> drawn = opponent.drawIntoHand(1);
			game.message(opponent, "You draw " + Card.htmlList(drawn));
			game.messageOpponents(opponent, opponent.username + " draws " + Card.numCards(drawn.size()));
		}
	}

}
