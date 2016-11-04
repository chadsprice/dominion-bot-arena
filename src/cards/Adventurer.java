package cards;

import server.Card;
import server.Game;
import server.Player;

public class Adventurer extends Card {

	public Adventurer() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public void onPlay(Player player, Game game) {
        // reveal cards from your deck until you have 2 treasure cards, put those into your hand and discard the rest
		revealUntil(player, game,
				c -> c.isTreasure, 2,
				list -> putRevealedIntoHand(player, game, list));
	}

	@Override
	public String[] description() {
		return new String[] {"Reveal cards from your deck until you reveal 2 Treasure cards.", "Put those Treasure cards into your hand and discard the other revealed cards."};
	}

	@Override
	public String toString() {
		return "Adventurer";
	}

}
