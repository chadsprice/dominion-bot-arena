package cards;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.*;

public class Bureaucrat extends Card {

	@Override
	public String name() {
		return "Bureaucrat";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {"Gain_a_[Silver] onto_your_deck. Each_other_player reveals a_Victory_card from_their_hand and puts_it on their_deck (or_reveals a_hand with no Victory_cards)."};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		// gain a Silver
		gainOntoDeck(player, game, Cards.SILVER);
		// each other player puts a victory card from their hand on top of their deck or reveals a hand with no victory cards
		for (Player target : targets) {
			Set<Card> victoryCards = target.getHand().stream()
					.filter(Card::isVictory)
					.collect(Collectors.toSet());
			if (!victoryCards.isEmpty()) {
				Card toPutOnDeck = promptChoosePutOnDeck(
					target,
					game,
					victoryCards,
					this.toString() + ": Choose a victory card to reveal and put on top of your deck.",
					Prompt.Type.DANGER
				);
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

}
