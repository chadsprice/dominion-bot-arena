package cards;

import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;

public class Mint extends Card {

	@Override
	public String name() {
		return "Mint";
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
		return new String[] {"You may reveal a_Treasure card from your_hand. Gain a_copy of_it."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		Set<Card> treasures = player.getHand().stream()
				.filter(Card::isTreasure)
				.collect(Collectors.toSet());
		if (!treasures.isEmpty()) {
			Card card = promptChooseGainCopyOfCardInHand(
					player,
					game,
					treasures,
					this.toString() + ": Choose a card to reveal from your hand and gain a copy of.",
					false
			);
			if (card != null) {
				if (game.supply.get(card) != 0) {
					game.messageAll("revealing " + card.htmlName() + " and gaining a copy of it");
					game.gain(player, card);
				} else {
					game.messageAll("revealing " + card.htmlName() + " and gaining nothing");
				}
			} else {
				game.messageAll("revealing nothing");
			}
		} else {
			game.message(player, "having no treasure in your hand");
			game.messageOpponents(player, "having no treasure in their hand");
		}
	}

}
