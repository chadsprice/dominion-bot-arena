package cards;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;

public class Bureaucrat extends Card {

	public Bureaucrat() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		// gain a silver card
		gainOntoDeck(player, game, Card.SILVER);
		// each other player puts a victory card from their hand on top of their deck or reveals a hand with no victory cards
		for (Player target : targets) {
			Set<Card> victoryCards = target.getHand().stream()
					.filter(c -> c.isVictory)
					.collect(Collectors.toSet());
			if (!victoryCards.isEmpty()) {
				Card toPutOnDeck = game.promptChoosePutOnDeck(target, victoryCards, "Bureaucrat: Choose a victory card to reveal and put on top of your deck.", "attackPrompt");
				game.message(target, "You reveal " + toPutOnDeck.htmlName() + " and put it on top of your deck");
				game.messageOpponents(target, target.username + " reveals " + toPutOnDeck.htmlName() + " and puts it on top of their deck");
				target.putFromHandOntoDraw(toPutOnDeck);
			} else {
				String handString = target.getHand().isEmpty() ? "an empty hand" : Card.htmlList(target.getHand());
				game.message(target, "You reveal " + handString);
				game.messageOpponents(target, target.username + " reveals " + handString);
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Gain a Silver onto your deck. Each other player reveals a Victory card from their hand and puts it on their deck (or reveals a hand with no Victory cards)."};
	}

	@Override
	public String toString() {
		return "Bureaucrat";
	}

}
