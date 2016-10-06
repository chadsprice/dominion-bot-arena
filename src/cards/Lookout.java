package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Lookout extends Card {

	public Lookout() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 1);
		List<Card> drawn = player.takeFromDraw(3);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// trash one
		if (!drawn.isEmpty()) {
			String[] choices = new String[drawn.size()];
			for (int i = 0; i < choices.length; i++) {
				choices[i] = drawn.get(i).toString();
			}
			int choice = game.promptMultipleChoice(player, "Lookout: Choose one to trash", choices);
			Card toTrash = drawn.get(choice);
			game.message(player, "trashing the " + toTrash.htmlNameRaw());
			game.messageOpponents(player, "trashing " + toTrash.htmlName());
			drawn.remove(choice);
			game.addToTrash(toTrash);
		}
		// discard one
		if (!drawn.isEmpty()) {
			String[] choices = new String[drawn.size()];
			for (int i = 0; i < choices.length; i++) {
				choices[i] = drawn.get(i).toString();
			}
			int choice = game.promptMultipleChoice(player, "Lookout: Choose one to discard", choices);
			Card toDiscard = drawn.get(choice);
			game.message(player, "discarding the " + toDiscard.htmlNameRaw());
			game.messageOpponents(player, "discarding " + toDiscard.htmlName());
			drawn.remove(choice);
			player.addToDiscard(toDiscard);
		}
		// put the other one on top of your deck
		if (!drawn.isEmpty()) {
			Card toPutOnDeck = drawn.get(0);
			game.message(player, "putting the " + toPutOnDeck.htmlNameRaw() + " on top of your deck");
			game.messageOpponents(player, "putting a card on top of his deck");
			player.putOnDraw(toPutOnDeck);
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Action", "Look at the top 3 cards of your deck. Trash one of them. Discard one of them. Put the other one on top of your deck."};
	}

	@Override
	public String toString() {
		return "Lookout";
	}

}
