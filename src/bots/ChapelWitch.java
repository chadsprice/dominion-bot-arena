package bots;

import server.Bot;
import server.Card;
import server.Cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChapelWitch extends Bot {

	@Override
	public String botName() {
		return "ChapelWitch";
	}

	@Override
	public List<Card> gainPriority() {
		List<Card> priority = new ArrayList<>();
		if (countInDeck(Cards.PLATINUM) > 0) {
			priority.add(Cards.COLONY);
		}
		if (countInSupply(Cards.COLONY) <= 6) {
			priority.add(Cards.PROVINCE);
		}
		if (countInDeck(Cards.WITCH) == 0) {
			priority.add(Cards.WITCH);
		}
		if (gainsToEndGame() <= 5) {
			priority.add(Cards.DUCHY);
		}
		if (gainsToEndGame() <= 2) {
			priority.add(Cards.ESTATE);
		}
		priority.add(Cards.PLATINUM);
		priority.add(Cards.GOLD);
		// if this bot somehow gets rid of its chapel later in the game, don't get another one
		if (getCoins() <= 3 && countInDeck(Cards.CHAPEL) == 0 && turns <= 2) {
			priority.add(Cards.CHAPEL);
		}
		priority.add(Cards.SILVER);
		if (gainsToEndGame() <= 3) {
			priority.add(Cards.COPPER);
		}
		return priority;
	}

	@Override
	public List<Card> trashPriority() {
		List<Card> priority = new ArrayList<>();
		priority.add(Cards.CURSE);
		if (gainsToEndGame() > 4) {
			priority.add(Cards.ESTATE);
		}
		if (getTotalMoney() > 4 && !(countInDeck(Cards.WITCH) == 0 && treasureInHand() == 5)) {
			priority.add(Cards.COPPER);
		}
		if (gainsToEndGame() > 2) {
			priority.add(Cards.ESTATE);
		}
		return priority;
	}

	private int treasureInHand() {
		int coins = 0;
		for (Card card : getHand()) {
			if (card.isTreasure) {
				coins += card.treasureValue(game);
			}
		}
		return coins;
	}

	@Override
	public Set<Card> required() {
		return new HashSet<>(Arrays.asList(Cards.CHAPEL, Cards.WITCH));
	}

}
