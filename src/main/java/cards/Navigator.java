package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import server.*;

public class Navigator extends Card {

	@Override
	public String name() {
		return "Navigator";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2$>",
				"Look at the_top 5_cards of your_deck. Either_discard all_of_them, or_put them back on_top of your_deck in_any_order."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
		// look at the top 5 cards of the deck
		List<Card> drawn = player.takeFromDraw(5);
		if (!drawn.isEmpty()) {
			game.message(player, "drawing " + Card.htmlList(drawn));
			game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
			if (chooseDiscard(player, game, drawn)) {
				// discard all of the drawn cards
				game.message(player, "discarding them");
				game.messageOpponents(player, "discarding " + Card.htmlList(drawn));
				player.addToDiscard(drawn);
			} else {
				// put them back on top of the deck in any order
				game.message(player, "putting them back on top of your deck");
				game.messageOpponents(player, "putting them back on top of their deck");
				putOnDeckInAnyOrder(
						player,
						game,
						drawn,
						this.toString() + ": Put the cards on top of your deck"
				);
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
	}

	private boolean chooseDiscard(Player player, Game game, List<Card> cards) {
		if (player instanceof Bot) {
			return ((Bot) player).navigatorDiscard(cards);
		}
		int choice = new Prompt(player, game)
				.message(this.toString() + ": you draw " + Card.htmlList(cards))
				.multipleChoices(new String[]{"Discard", "Put back on top"})
				.responseMultipleChoiceIndex();
		return (choice == 0);
	}

}
