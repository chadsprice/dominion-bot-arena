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
			plusCards(player, game, 2);
			break;
		case 1:
			plusCoins(player, game, 2);
			break;
		default:
			// Trash 2 cards
			List<Card> toTrash = null;
			if (player.getHand().size() > 0) {
				toTrash = game.promptTrashNumber(player, 2, true, "Steward");
				player.removeFromHand(toTrash);
				game.trash(player, toTrash);
			} else {
				toTrash = new ArrayList<Card>();
			}
			game.messageAll("trashing " + Card.htmlList(toTrash));
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
