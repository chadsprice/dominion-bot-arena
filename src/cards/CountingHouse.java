package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class CountingHouse extends Card {

	public CountingHouse() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		int numCoppersInDiscard = 0;
		for (Card card : player.getDiscard()) {
			if (card == Cards.COPPER) {
				numCoppersInDiscard++;
			}
		}
		if (numCoppersInDiscard != 0) {
			String[] choices = new String[numCoppersInDiscard + 1];
			for (int i = 0; i <= numCoppersInDiscard; i++) {
				choices[i] = (numCoppersInDiscard - i) + "";
			}
			int choice = game.promptMultipleChoice(player, "Counting House: Put how many " + Cards.COPPER.htmlNameRaw() + " cards from your discard into your hand?" , choices);
			int toTake = numCoppersInDiscard - choice;
			if (toTake != 0) {
				game.message(player, "revealing " + Cards.COPPER.htmlName(toTake) + " from your discard and putting them into your hand");
				game.messageOpponents(player, "revealing " + Cards.COPPER.htmlName(toTake) + " from their discard and putting them into your hand");
				player.removeFromDiscard(Cards.COPPER, toTake);
				player.addToHand(Cards.COPPER, toTake);
			}
		} else {
			game.message(player, "having no " + Cards.COPPER.htmlNameRaw() + " in your discard");
			game.messageOpponents(player, "having no " + Cards.COPPER.htmlNameRaw() + " in their discard");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Look through your discard pile, reveal any number of Copper cards from it, and put them into your hand."};
	}

	@Override
	public String toString() {
		return "Counting House";
	}

}
