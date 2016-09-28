package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BmLibraryBot extends Bot {

	public BmLibraryBot() {
		super();
		setName("BM Library");
	}

	@Override
	public List<Card> gainPriority() {
		List<Card> priority = new ArrayList<Card>();
		if (countInDeck(Card.PLATINUM) > 0) {
			priority.add(Card.COLONY);
		}
		if (countInSupply(Card.COLONY) <= 6) {
			priority.add(Card.PROVINCE);
		}
		if (gainsToEndGame() <= 5) {
			priority.add(Card.DUCHY);
		}
		if (gainsToEndGame() <= 2) {
			priority.add(Card.ESTATE);
		}
		priority.add(Card.PLATINUM);
		priority.add(Card.GOLD);
		priority.add(Card.LIBRARY);
		priority.add(Card.SILVER);
		return priority;
	}

	@Override
	public Set<Card> required() {
		return new HashSet<Card>(Arrays.asList(new Card[] {Card.LIBRARY}));
	}

}
