package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class ShantyTown extends Card {

	@Override
	public String name() {
		return "Shanty Town";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2_Actions>",
				"Reveal your hand. If_you have no_Action cards in_hand, <+2_Cards>."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 2);
		// reveal your hand
		revealHand(player, game);
		// if you have no actions in hand, +2 cards
		if (player.getHand().stream().noneMatch(Card::isAction)) {
			plusCards(player, game, 2);
		}
	}

}
