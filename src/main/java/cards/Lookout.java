package cards;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import server.*;

public class Lookout extends Card {

	@Override
	public String name() {
		return "Lookout";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Action>",
				"Look at the_top 3_cards of your_deck. Trash one of them. Discard one of them. Put the other one on_top_of your_deck."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 1);
		List<Card> drawn = player.takeFromDraw(3);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// trash one
		if (!drawn.isEmpty()) {
			Card toTrash = chooseTrash(player, game, drawn);
			game.message(player, "trashing the " + toTrash.htmlNameRaw());
			game.messageOpponents(player, "trashing " + toTrash.htmlName());
			drawn.remove(toTrash);
			game.trash(player, toTrash);
		}
		// discard one
		if (!drawn.isEmpty()) {
			Card toDiscard = chooseDiscard(player, game, drawn);
			game.message(player, "discarding the " + toDiscard.htmlNameRaw());
			game.messageOpponents(player, "discarding " + toDiscard.htmlName());
			drawn.remove(toDiscard);
			player.addToDiscard(toDiscard);
		}
		// put the other one on top of your deck
		if (!drawn.isEmpty()) {
			Card toPutOnDeck = drawn.get(0);
			game.message(player, "putting the " + toPutOnDeck.htmlNameRaw() + " on top of your deck");
			game.messageOpponents(player, "putting a card on top of their deck");
			player.putOnDraw(toPutOnDeck);
		}
	}

	private Card chooseTrash(Player player, Game game, List<Card> cards) {
		if (player instanceof Bot) {
			Card toTrash = ((Bot) player).lookoutTrash(Collections.unmodifiableList(cards));
			checkContains(cards, toTrash);
			return toTrash;
		}
		return promptMultipleChoiceCard(
				player,
				game,
				Prompt.Type.DANGER,
				this.toString() + ": Choose one to trash",
				cards
		);
	}

	private Card chooseDiscard(Player player, Game game, List<Card> cards) {
		if (player instanceof Bot) {
			Card toDiscard = ((Bot) player).lookoutDiscard(Collections.unmodifiableList(cards));
			checkContains(cards, toDiscard);
			return toDiscard;
		}
		return promptMultipleChoiceCard(
				player,
				game,
				Prompt.Type.NORMAL,
				this.toString() + ": Choose one to discard" + (cards.size() > 1 ? " (the other will go on top of your deck)" : ""),
				cards
		);
	}

}
