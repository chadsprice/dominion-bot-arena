package bots;

import server.Bot;
import server.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BankWharfBot extends Bot {

	public BankWharfBot() {
		super();
		setName("BankWharf");
	}

	@Override
	public List<Card> gainPriority() {
		// based on Geronimoo and Jorbles's "BankWharf" bot
		List<Card> priority = new ArrayList<Card>();
		if (countInDeck(Card.PLATINUM) > 0) {
			priority.add(Card.COLONY);
		}
		if (countInSupply(Card.COLONY) <= 6) {
			priority.add(Card.PROVINCE);
		}
		if (gainsToEndGame() <= 4) {
			priority.add(Card.DUCHY);
		}
		if (gainsToEndGame() <= 2) {
			priority.add(Card.ESTATE);
		}
		priority.add(Card.PLATINUM);
		priority.add(Card.BANK);
		priority.add(Card.GOLD);
		priority.add(Card.WHARF);
		priority.add(Card.SILVER);
		if (gainsToEndGame() <= 3) {
			priority.add(Card.COPPER);
		}
		return priority;
	}

	@Override
	public Set<Card> required() {
		return new HashSet<Card>(Arrays.asList(new Card[] {Card.BANK, Card.WHARF}));
	}

}
