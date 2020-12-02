package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Venture extends Card {

	@Override
	public String name() {
		return "Venture";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{
				"1$",
				"When you play this, reveal_cards from your_deck until you reveal a_Treasure. Discard the other_cards.",
				"Play that Treasure."
		};
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

	// Java lambdas can't set local variables, so use this object to hold a Card variable that can be set
	private static class CardHolder {
		Card held;
	}

	@Override
	public void onPlay(Player player, Game game) {
		CardHolder holder = new CardHolder();
		// reveal cards from the top of the deck until a treasure is revealed
		// discard the other cards before playing the treasure
		revealUntil(
				player,
				game,
				Card::isTreasure,
				c -> holder.held = c
		);
		if (holder.held != null) {
			// play the revealed treasure
			game.messageAll("playing the " + holder.held.htmlNameRaw());
			game.playTreasure(player, holder.held);
		}
	}

}
