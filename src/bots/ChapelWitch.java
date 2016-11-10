package bots;

import server.Bot;
import server.Card;

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
		if (countInDeck(Card.PLATINUM) > 0) {
			priority.add(Card.COLONY);
		}
		if (countInSupply(Card.COLONY) <= 6) {
			priority.add(Card.PROVINCE);
		}
		if (countInDeck(Card.WITCH) == 0) {
			priority.add(Card.WITCH);
		}
		if (gainsToEndGame() <= 5) {
			priority.add(Card.DUCHY);
		}
		if (gainsToEndGame() <= 2) {
			priority.add(Card.ESTATE);
		}
		priority.add(Card.PLATINUM);
		priority.add(Card.GOLD);
		// if this bot somehow gets rid of its chapel later in the game, don't get another one
		if (getCoins() <= 3 && countInDeck(Card.CHAPEL) == 0 && turns <= 2) {
			priority.add(Card.CHAPEL);
		}
		priority.add(Card.SILVER);
		if (gainsToEndGame() <= 3) {
			priority.add(Card.COPPER);
		}
		return priority;
	}

	@Override
	public List<Card> trashPriority() {
		List<Card> priority = new ArrayList<>();
		priority.add(Card.CURSE);
		if (gainsToEndGame() > 4) {
			priority.add(Card.ESTATE);
		}
		if (getTotalMoney() > 4 && !(countInDeck(Card.WITCH) == 0 && treasureInHand() == 5)) {
			priority.add(Card.COPPER);
		}
		if (gainsToEndGame() > 2) {
			priority.add(Card.ESTATE);
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
		return new HashSet<>(Arrays.asList(Card.CHAPEL, Card.WITCH));
	}

}
