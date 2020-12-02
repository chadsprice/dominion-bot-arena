package cards;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;

public class Scout extends Card {

	@Override
	public String name() {
		return "Scout";
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
				"<+1_Action>",
				"Reveal the_top 4_cards of your_deck.",
				"Put the revealed Victory cards into your_hand. Put the_other cards on_top of your_deck in_any_order."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 1);
		// reveal 4 cards, etc.
		List<Card> revealed = player.takeFromDraw(4);
		if (!revealed.isEmpty()) {
			// reveal top 4 cards
			game.messageAll("drawing " + Card.htmlList(revealed));
			List<Card> victoryCards = revealed.stream()
					.filter(Card::isVictory)
					.collect(Collectors.toList());
			List<Card> nonVictoryCards = revealed.stream()
					.filter(c -> !c.isVictory())
					.collect(Collectors.toList());
			// add revealed victory cards to hand
			if (!victoryCards.isEmpty()) {
				game.message(player, "putting " + Card.htmlList(victoryCards) + " into your hand");
				game.messageOpponents(player, "putting " + Card.htmlList(victoryCards) + " into their hand");
				player.addToHand(victoryCards);
			}
			// put the rest on top of your deck in any order
			if (!nonVictoryCards.isEmpty()) {
				game.message(player, "putting the rest on top of your deck");
				game.messageOpponents(player, "putting the rest on top of their deck");
				putOnDeckInAnyOrder(
						player,
						game,
						revealed,
						this.toString() + ": Put the remaining cards on top of your deck"
				);
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
	}

}
