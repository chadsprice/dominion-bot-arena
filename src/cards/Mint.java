package cards;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;

public class Mint extends Card {

	public Mint() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		Set<Card> treasures = player.getHand().stream()
				.filter(c -> c.isTreasure)
				.collect(Collectors.toSet());
		if (!treasures.isEmpty()) {
			Card card = game.promptChooseGainCopyOfCardInHand(player, treasures, "Mint: Choose a card to reveal from your hand and gain a copy of.");
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

	@Override
	public String[] description() {
		return new String[] {"You may reveal a Treasure card from your hand. Gain a copy of it."};
	}

	@Override
	public String toString() {
		return "Mint";
	}

}
