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
		// reveal cards from the draw until 2 treasures have been revealed, or the draw runs out
		while (treasures.size() < 2) {
			List<Card> drawn = player.takeFromDraw(1);
			if (drawn.isEmpty()) {
				// the draw has run out
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
		if (!revealed.isEmpty()) {
			game.messageAll("revealing " + Card.htmlList(revealed));
			String treasuresStr = treasures.isEmpty() ? "nothing" : Card.htmlList(treasures);
			String setAsideStr = setAside.isEmpty() ? "" : " and discarding the rest";
			game.message(player, "putting " + treasuresStr + " into your hand" + setAsideStr);
			game.messageOpponents(player, "putting " + treasuresStr + " into their hand" + setAsideStr);
			// put drawn treasures in the hand
			if (!treasures.isEmpty()) {
				player.addToHand(treasures);
			}
			// put the rest in the discard
			if (!setAside.isEmpty()) {
				player.addToDiscard(setAside);
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
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
