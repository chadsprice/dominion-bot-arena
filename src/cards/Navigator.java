package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Navigator extends Card {

	public Navigator() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +$2
		player.addExtraCoins(2);
		game.message(player, "... You get +$2");
		game.messageOpponents(player, "... getting +$2");
		// look at the top 5 cards of the deck
		List<Card> drawn = player.takeFromDraw(5);
		if (!drawn.isEmpty()) {
			game.message(player, "... You draw " + Card.htmlList(drawn));
			game.messageOpponents(player, "... drawing " + drawn.size() + " card(s)");
			int choice = game.promptMultipleChoice(player, "Navigator: you draw " + Card.htmlList(drawn), new String[]{"Discard", "Put back on top"});
			if (choice == 0) {
				// discard all of the drawn cards
				player.addToDiscard(drawn);
				game.message(player, "... You discard them");
				game.messageOpponents(player, "... discarding them");
			} else {
				// put them back on top of the deck in any order
				Collections.sort(drawn, Player.HAND_ORDER_COMPARATOR);
				List<Card> toPutOnDeck = new ArrayList<Card>();
				while (!drawn.isEmpty()) {
					String[] choices = new String[drawn.size()];
					for (int i = 0; i < drawn.size(); i++) {
						choices[i] = drawn.get(i).toString();
					}
					int next = game.promptMultipleChoice(player, "Navigator: Put the cards on top of your deck (the first card you choose will be on top of your deck)", choices);
					toPutOnDeck.add(drawn.remove(next));
				}
				player.putOnDraw(toPutOnDeck);
				game.message(player, "... You put them back on top of your deck");
				game.messageOpponents(player, "... putting them back on top of his deck");
			}
		} else {
			game.message(player, "... Your deck is empty");
			game.messageOpponents(player, "... having an empty deck");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "Look at the top 5 cards of your deck. Either discard all of them, or put them back on top of your deck in any order."};
	}

	@Override
	public String toString() {
		return "Navigator";
	}

}
