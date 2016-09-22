package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Loan extends Card {

	public Loan() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
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
			int choice = game.promptMultipleChoice(player, "You reveal " + treasure.htmlName() + ", discard or trash it?", new String[] {"Discard", "Trash"});
			if (choice == 0) {
				player.addToDiscard(treasure);
				game.messageAll("discarding the " + treasure.htmlNameRaw());
			} else {
				game.trash.add(treasure);
				game.messageAll("trashing the " + treasure.htmlNameRaw());
			}
		}
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

	@Override
	public String[] description() {
		return new String[]{"$1", "When you play this, reveal cards from your deck until you reveal a Treasure.", "Discard it or trash it.", "Discard the other cards."};
	}

	@Override
	public String toString() {
		return "Loan";
	}

}
