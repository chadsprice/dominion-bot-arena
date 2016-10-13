package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Library extends Card {

	public Library() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		List<Card> setAside = new ArrayList<>();
		List<Card> addedToHand = new ArrayList<>();
		// draw until 7 in hand
		while (player.getHand().size() < 7) {
			List<Card> drawn = player.takeFromDraw(1);
			if (drawn.size() == 0) {
				game.message(player, "your deck is empty");
				game.messageOpponents(player, "their deck is empty");
				break;
			}
			Card card = drawn.get(0);
			// allow action cards to be set aside
			if (card.isAction) {
				int choice = game.promptMultipleChoice(player, "Library: You draw " + card.htmlName() + ", set it aside?", new String[] {"Set aside", "Keep"});
				if (choice == 0) {
					// if one is set aside, announce the number drawn before it and what the set aside card is
					if (addedToHand.size() > 0) {
						game.message(player, "drawing " + Card.htmlList(addedToHand));
						game.messageOpponents(player, "drawing " + Card.numCards(addedToHand.size()));
						addedToHand.clear();
					}
					setAside.add(card);
					game.messageAll("setting aside " + card.htmlName());
					continue;
				}
			}
			player.addToHand(card);
			addedToHand.add(card);
		}
		if (!addedToHand.isEmpty()) {
			game.message(player, "drawing " + Card.htmlList(addedToHand));
			game.messageOpponents(player, "drawing " + Card.numCards(addedToHand.size()));
		}
		if (!setAside.isEmpty()) {
			player.addToDiscard(setAside);
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards."};
	}

	@Override
	public String toString() {
		return "Library";
	}

	@Override
	public String plural() {
		return "Libraries";
	}

}
