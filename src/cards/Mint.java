package cards;

import java.util.HashSet;
import java.util.Set;

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
		Set<Card> treasures = treasuresInHand(player);
		if (!treasures.isEmpty()) {
			Card card = game.promptChooseGainCopyOfCardInHand(player, treasures, "Mint: Choose a card to reveal from your hand and gain a copy of");
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
			game.messageOpponents(player, "having no treasure in his hand");
		}
	}

	private Set<Card> treasuresInHand(Player player) {
		Set<Card> treasures = new HashSet<Card>();
		for (Card card : player.getHand()) {
			if (card.isTreasure) {
				treasures.add(card);
			}
		}
		return treasures;
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
