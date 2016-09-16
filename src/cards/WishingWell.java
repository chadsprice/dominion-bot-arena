package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class WishingWell extends Card {

	public WishingWell() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// +1 action
		player.addActions(1);
		game.messageAll("getting +1 action");
		// Name a card
		drawn = player.takeFromDraw(1);
		if (drawn.size() == 1) {
			// TODO add a nameACard method to Game.java
			Card namedCard = game.promptChooseGainFromSupply(player, game.supply.keySet(), "Wishing Well: Name a card", false, "Name a card that is not in the supply");
			if (namedCard == null) {
				// find all cards not in the supply
				Set<Card> cardsNotInSupply = new HashSet<Card>(Card.cardsByName.values());
				cardsNotInSupply.removeAll(game.supply.keySet());
				// create an alphabet of only the first letters of cards not in the supply
				Set<Character> letters = new HashSet<Character>();
				for (Card cardNotInSupply : cardsNotInSupply) {
					letters.add(cardNotInSupply.toString().charAt(0));
				}
				List<Character> orderedLetters = new ArrayList<Character>(letters);
				Collections.sort(orderedLetters);
				String[] choices = new String[orderedLetters.size()];
				for (int i = 0; i < orderedLetters.size(); i++) {
					choices[i] = orderedLetters.get(i) + "";
				}
				char chosenLetter = orderedLetters.get(game.promptMultipleChoice(player, "Wishing Well: Select the first letter of the card you want to name", choices));
				// find all cards not in the supply starting with the chosen letter
				List<String> names = new ArrayList<String>();
				for (Card cardNotInSupply : cardsNotInSupply) {
					String name = cardNotInSupply.toString();
					if (name.charAt(0) == chosenLetter) {
						names.add(name);
					}
				}
				Collections.sort(names);
				choices = new String[names.size()];
				choices = (String[]) names.toArray(choices);
				String chosenName = choices[game.promptMultipleChoice(player, "Wishing Well: Select the card you want to name", choices)];
				namedCard = Card.fromName(chosenName);
			}
			Card revealedCard = drawn.get(0);
			if (namedCard == revealedCard) {
				player.addToHand(revealedCard);
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it into your hand");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it into his hand");
			} else {
				player.putOnDraw(revealedCard);
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it back");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it back");
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "his deck is empty");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Name a card. Reveal the top card of your deck. If it's the named card, put it into your hand."};
	}

	@Override
	public String toString() {
		return "Wishing Well";
	}

}
