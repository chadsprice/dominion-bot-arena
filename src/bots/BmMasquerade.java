package bots;

import server.Bot;
import server.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BmMasquerade extends Bot {

	@Override
	public String botName() {
		return "BM Masquerade";
	}

	@Override
	public List<Card> gainPriority() {
		// based on ehunt's "BM Maquerade" bot
		List<Card> priority = new ArrayList<>();
		priority.add(Card.PROVINCE);
		priority.add(Card.GOLD);
		if (gainsToEndGame() <= 5) {
			priority.add(Card.DUCHY);
		}
		if (countInDeck(Card.MASQUERADE) == 0) {
			priority.add(Card.MASQUERADE);
		}
		priority.add(Card.SILVER);
		return priority;
	}

	@Override
	public Set<Card> required() {
		return new HashSet<>(Arrays.asList(new Card[] {Card.MASQUERADE}));
	}

}
