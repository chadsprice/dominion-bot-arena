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
		// +2 actions
		player.addActions(2);
		game.message(player, "... You get +2 actions");
		game.messageOpponents(player, "... getting +2 actions");
		// reveal your hand
		game.message(player, "... You reveal " + Card.htmlList(player.getHand()));
		game.messageOpponents(player, "... revealing " + Card.htmlList(player.getHand()));
		// if no actions, +2 cards
		if (!handContainsActions(player)) {
			List<Card> drawn = player.drawIntoHand(2);
			game.message(player, "... You draw " + Card.htmlList(drawn));
			game.messageOpponents(player, "... drawing " + drawn.size() + " card(s)");
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
		return new String[] {"+2 Actions", "Reveal your hand.", "If you have no Action cards in hand, +2 Cards."};
	}

	@Override
	public String toString() {
		return "Shanty Town";
	}

}
