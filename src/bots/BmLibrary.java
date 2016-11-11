package bots;

import server.Bot;
import server.Card;
import server.Cards;

import java.util.*;

public class BmLibrary extends Bot {

	@Override
	public String botName() {
		return "BM Library";
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
		if (gainsToEndGame() <= 5) {
			priority.add(Cards.DUCHY);
		}
		if (gainsToEndGame() <= 2) {
			priority.add(Cards.ESTATE);
		}
		priority.add(Cards.PLATINUM);
		priority.add(Cards.GOLD);
		priority.add(Cards.LIBRARY);
		priority.add(Cards.SILVER);
		return priority;
	}

	@Override
	public Set<Card> required() {
		return Collections.singleton(Cards.LIBRARY);
	}

}
