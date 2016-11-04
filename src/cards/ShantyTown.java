package cards;

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
		// if you have no actions in hand, +2 cards
		if (player.getHand().stream().noneMatch(c -> c.isAction)) {
			plusCards(player, game, 2);
		}
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
