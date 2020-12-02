package cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import server.*;

public class Library extends Card {

	@Override
	public String name() {
		return "Library";
	}

	@Override
	public String plural() {
		return "Libraries";
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
		return new String[] {"Draw until you have 7_cards in_hand, skipping any Action cards you choose_to; set_those_aside, discarding them afterwards."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		List<Card> setAside = new ArrayList<>();
		List<Card> addedToHand = new ArrayList<>();
		boolean isDeckEmpty = false;
		// draw until 7 in hand
		while (player.getHand().size() < 7) {
			List<Card> drawn = player.takeFromDraw(1);
			if (drawn.isEmpty()) {
				isDeckEmpty = true;
				break;
			}
			Card card = drawn.get(0);
			// allow any action card to be set aside
			if (card.isAction() && chooseSetAside(player, game, card)) {
				// announce the cards put in hand before the set aside card
				if (!addedToHand.isEmpty()) {
					game.message(player, "drawing " + Card.htmlList(addedToHand));
					game.messageOpponents(player, "drawing " + Card.numCards(addedToHand.size()));
					addedToHand.clear();
				}
				// announce the set aside card
				game.messageAll("setting aside " + card.htmlName());
				setAside.add(card);
			} else {
				player.addToHand(card);
				addedToHand.add(card);
			}
		}
		// announce the last cards put in hand
		if (!addedToHand.isEmpty()) {
			game.message(player, "drawing " + Card.htmlList(addedToHand));
			game.messageOpponents(player, "drawing " + Card.numCards(addedToHand.size()));
		}
		if (isDeckEmpty) {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
		// discard set aside cards
		if (!setAside.isEmpty()) {
			player.addToDiscard(setAside);
		}
	}

	private boolean chooseSetAside(Player player, Game game, Card card) {
		if (player instanceof Bot) {
			return ((Bot) player).librarySetAside(card);
		}
		int choice = new Prompt(player, game)
				.message(this.toString() + ": You draw " + card.htmlName() + ". Set it aside?")
				.multipleChoices(new String[] {"Set aside", "Keep"})
				.responseMultipleChoiceIndex();
        return (choice == 0);
	}

}
