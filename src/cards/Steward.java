package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Steward extends Card {

	public Steward() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		String[] choices = new String[] {"+2 cards", "+$2", "Trash 2 cards"};
		int choice = game.promptMultipleChoice(player, "Steward: Choose one", choices);
		switch (choice) {
		case 0:
			// +2 cards
			List<Card> drawn = player.drawIntoHand(2);
			game.message(player, "... You draw " + Card.htmlList(drawn));
			game.messageOpponents(player, "... drawing " + drawn.size() + " card(s)");
			break;
		case 1:
			// +$2
			player.addExtraCoins(2);
			game.message(player, "... You get +$2");
			game.messageOpponents(player, "... getting +$2");
			break;
		default:
			// Trash 2 cards
			List<Card> toTrash = null;
			if (player.getHand().size() > 0) {
				int trashNumber = player.getHand().size() == 1 ? 1 : 2;
				toTrash = game.promptDiscardNumber(player, trashNumber, true, "Steward", "trash");
				player.removeFromHand(toTrash);
				game.trash.addAll(toTrash);
			} else {
				toTrash = new ArrayList<Card>();
			}
			game.message(player, "... You trash " + Card.htmlList(toTrash));
			game.messageOpponents(player, "... trashing " + Card.htmlList(toTrash));
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Choose one: +2 Cards; or +$2; or trash 2 cards from your hand."};
	}

	@Override
	public String toString() {
		return "Steward";
	}

}
