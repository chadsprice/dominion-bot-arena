package cards;

import server.Card;
import server.Game;
import server.Player;

public class Venture extends Card {

	public Venture() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 5;
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
		revealUntil(player, game,
				c -> c.isTreasure,
				c -> holder.held = c);
		if (holder.held != null) {
			// play the revealed treasure
			game.messageAll("playing the " + holder.held.htmlNameRaw());
			game.playTreasure(player, holder.held);
		}
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

	@Override
	public String[] description() {
		return new String[]{"$1", "When you play this, reveal cards from your deck until you reveal a Treasure. Discard the other cards.", "Play that treasure."};
	}

	@Override
	public String toString() {
		return "Venture";
	}

}
