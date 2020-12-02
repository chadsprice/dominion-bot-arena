package cards;

import server.*;

import java.util.Set;

public class CountingHouse extends Card {

	@Override
	public String name() {
		return "Counting House";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {"Look through your discard_pile, reveal any_number of [Copper] cards from it, and put_them into your_hand."};
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
			int toTake = chooseTakeCoppersFromDiscard(player, game,numCoppersInDiscard);
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

	private int chooseTakeCoppersFromDiscard(Player player, Game game, int numCoppersInDiscard) {
	    if (player instanceof Bot) {
	        int toTake = ((Bot) player).countingHouseTakeCoppersFromDiscard(numCoppersInDiscard);
	        checkMultipleChoice(numCoppersInDiscard + 1, toTake);
	        return toTake;
        }
        String[] choices = new String[numCoppersInDiscard + 1];
        for (int i = 0; i <= numCoppersInDiscard; i++) {
            choices[i] = (numCoppersInDiscard - i) + "";
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Put how many " + Cards.COPPER.htmlNameRaw() + " cards from your discard into your hand?")
                .multipleChoices(choices)
                .responseMultipleChoiceIndex();
        return numCoppersInDiscard - choice;
    }

}
