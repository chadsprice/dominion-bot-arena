package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class ShantyTown extends Card {

	public ShantyTown() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 2);
		// reveal your hand
		revealHand(player, game);
		// if no actions, +2 cards
		if (!handContainsActions(player)) {
			List<Card> drawn = player.drawIntoHand(2);
			game.message(player, "drawing " + Card.htmlList(drawn));
			game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		}
	}

	private boolean handContainsActions(Player player) {
		for (Card card : player.getHand()) {
			if (card.isAction) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Actions", "Reveal your hand. If you have no Action cards in hand, +2 Cards."};
	}

	@Override
	public String toString() {
		return "Shanty Town";
	}

}
