package cards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		if (game.supply.get(Card.SILVER) > 0) {
			game.message(player, "gaining " + Card.SILVER.htmlName() + " and putting it on top of your deck");
			game.messageOpponents(player, "gaining " + Card.SILVER.htmlName() + " and putting it on top of his deck");
			game.gainToTopOfDeck(player, Card.SILVER);
		}
		// each other player puts a victory card from his hand on top of his deck or reveals a hand with no victory cards
		for (Player target : targets) {
			Set<Card> choices = victoryCardsInHand(target);
			if (!choices.isEmpty()) {
				Card choice = game.promptChoosePutOnDeck(target, choices, "Bureaucrat: Choose a victory card to reveal and put on top of your deck", "attackPrompt");
				target.removeFromHand(choice);
				target.putOnDraw(choice);
				game.message(target, "You reveal " + choice.htmlName() + " and put it on top of your deck");
				game.messageOpponents(target, target.username + " reveals " + choice.htmlName() + " and puts it on top of his deck");
			} else {
				String handString = target.getHand().isEmpty() ? "an empty hand" : Card.htmlList(target.getHand());
				game.message(target, "You reveal " + handString);
				game.messageOpponents(target, target.username + " reveals " + handString);
			}
		}
	}

	private Set<Card> victoryCardsInHand(Player player) {
		Set<Card> victoryCards = new HashSet<Card>();
		for (Card card : player.getHand()) {
			if (card.isVictory) {
				victoryCards.add(card);
			}
		}
		return victoryCards;
	}

	@Override
	public String[] description() {
		return new String[] {"Gain a Silver card; put it on top of your deck.", "Each other player reveals a Victory card from his hand and puts it on his deck (or reveals a hand with no Victory cards)."};
	}

	@Override
	public String toString() {
		return "Bureaucrat";
	}

}
