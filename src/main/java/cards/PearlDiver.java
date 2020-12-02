package cards;

import server.*;

import java.util.Set;

public class PearlDiver extends Card {

	@Override
	public String name() {
		return "Pearl Diver";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Card>",
				"<+1_Action>",
				"Look at the bottom_card of your_deck. You_may put_it on_top."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		// look at bottom of deck
		Card card = player.bottomOfDeck();
		if (card != null) {
			if (choosePutOnTopOfDeck(player, game, card)) {
				game.message(player, "putting the " + card.htmlNameRaw() + " on top of your deck");
				game.messageOpponents(player, "putting the card at the bottom of their deck on top");
				player.putOnDraw(player.takeFromBottomOfDeck());
			} else {
				game.message(player, "leaving the " + card.htmlNameRaw() + " at the bottom of your deck");
				game.messageOpponents(player, "leaving the card at the bottom of their deck");
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
	}

	private boolean choosePutOnTopOfDeck(Player player, Game game, Card card) {
		if (player instanceof Bot) {
			return ((Bot) player).pearlDiverPutOnTopOfDeck(card);
		}
		int choice = new Prompt(player, game)
				.message(this.toString() + ": You see " + card.htmlName() + ". Put it on top of your deck?")
				.multipleChoices(new String[] {"Put on top", "Leave on bottom"})
				.responseMultipleChoiceIndex();
		return (choice == 0);
	}

}
