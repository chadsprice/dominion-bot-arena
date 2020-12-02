package bots;

import server.Bot;
import server.Card;
import server.Cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BankWharf extends Bot {

	@Override
	public String botName() {
		return "BankWharf";
	}

	@Override
	public List<Card> gainPriority() {
		// based on Geronimoo and Jorbles's "BankWharf" bot
		List<Card> priority = new ArrayList<>();
		if (countInDeck(Cards.PLATINUM) > 0) {
			priority.add(Cards.COLONY);
		}
		if (countInSupply(Cards.COLONY) <= 6) {
			priority.add(Cards.PROVINCE);
		}
		if (gainsToEndGame() <= 4) {
			priority.add(Cards.DUCHY);
		}
		if (gainsToEndGame() <= 2) {
			priority.add(Cards.ESTATE);
		}
		priority.add(Cards.PLATINUM);
		priority.add(Cards.BANK);
		priority.add(Cards.GOLD);
		priority.add(Cards.WHARF);
		priority.add(Cards.SILVER);
		if (gainsToEndGame() <= 3) {
			priority.add(Cards.COPPER);
		}
		return priority;
	}

	@Override
	public Set<Card> required() {
		return new HashSet<>(Arrays.asList(Cards.BANK, Cards.WHARF));
	}

}
