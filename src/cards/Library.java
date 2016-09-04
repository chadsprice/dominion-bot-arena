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
		int numDrawn = 0;
		while (player.getHand().size() < 7) {
			List<Card> drawn = player.takeFromDraw(1);
			if (drawn.size() == 0) {
				game.message(player, "... You draw " + numDrawn + " card(s)");
				game.messageOpponents(player, "... drawing " + numDrawn + " card(s)");
				numDrawn = 0;
				break;
			}
			Card card = drawn.get(0);
			if (card.isAction) {
				int choice = game.promptMultipleChoice(player, "Library: You draw " + card.htmlName() + "; set it aside?", new String[] {"Set aside", "Keep"});
				if (choice == 0) {
					if (numDrawn > 0) {
						game.message(player, "... You draw " + numDrawn + " card(s)");
						game.messageOpponents(player, "... drawing " + numDrawn + " card(s)");
						numDrawn = 0;
					}
					setAside.add(card);
					game.message(player, "... You set aside " + card.htmlName());
					game.messageOpponents(player, "... setting aside " + card.htmlName());
				} else {
					player.addToHand(card);
					numDrawn++;
				}
			} else {
				player.addToHand(card);
				numDrawn++;
			}
		}
		if (numDrawn > 0) {
			game.message(player, "... You draw " + numDrawn + " card(s)");
			game.messageOpponents(player, "... drawing " + numDrawn + " card(s)");
			numDrawn = 0;
		}
		if (setAside.size() > 0) {
			player.addToDiscard(setAside);
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Draw until you have 7 cards in hand.", "You may set aside any Action cards drawn this way, as you draw them; discard the set aside cards after you finish drawing."};
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
