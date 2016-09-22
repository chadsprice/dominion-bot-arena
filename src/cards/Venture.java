package cards;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public void onPlay(Player player, Game game) {
		// reveal cards from the top of the deck until a treasure is revealed
		List<Card> revealed = new ArrayList<Card>();
		Card treasure = null;
		for (;;) {
			List<Card> drawn = player.takeFromDraw(1);
			if (drawn.isEmpty()) {
				// revealed entire deck with no treasures
				break;
			}
			Card card = drawn.get(0);
			revealed.add(card);
			if (card.isTreasure) {
				// revealed a treasure
				treasure = card;
				break;
			}
		}
		if (revealed.isEmpty()) {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "his deck is empty");
			return;
		}
		game.messageAll("revealing " + Card.htmlList(revealed));
		if (treasure != null) {
			revealed.remove(treasure);
		}
		// discard the rest
		player.addToDiscard(revealed);
		if (treasure != null) {
			// play the revealed treasure
			game.messageAll("playing the " + treasure.htmlNameRaw());
			game.playTreasure(player, treasure);
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