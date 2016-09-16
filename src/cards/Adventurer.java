package cards;

import java.util.ArrayList;
import java.util.List;

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
		List<Card> revealed = new ArrayList<Card>();
		List<Card> treasures = new ArrayList<Card>();
		List<Card> setAside = new ArrayList<Card>();
		// draw at most 2 treasures
		while (treasures.size() < 2) {
			List<Card> drawn = player.takeFromDraw(1);
			if (drawn.size() == 0) {
				break;
			}
			Card card = drawn.get(0);
			revealed.add(card);
			if (card.isTreasure) {
				treasures.add(card);
			} else {
				setAside.add(card);
			}
		}
		// put drawn treasures in the hand
		if (treasures.size() > 0) {
			player.addToHand(treasures);
		}
		// put the rest in the discard
		if (setAside.size() > 0) {
			player.addToDiscard(setAside);
		}
		game.messageAll("revealing " + Card.htmlList(revealed));
		game.messageAll("drawing " + Card.htmlList(treasures));
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
