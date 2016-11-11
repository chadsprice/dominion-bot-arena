package bots;

import server.Bot;
import server.Card;
import server.Cards;

import java.util.*;

public class BmMasquerade extends Bot {

	@Override
	public String botName() {
		return "BM Masquerade";
	}

	@Override
	public List<Card> gainPriority() {
		// based on ehunt's "BM Maquerade" bot
		List<Card> priority = new ArrayList<>();
		priority.add(Cards.PROVINCE);
		priority.add(Cards.GOLD);
		if (gainsToEndGame() <= 5) {
			priority.add(Cards.DUCHY);
		}
		if (countInDeck(Cards.MASQUERADE) == 0) {
			priority.add(Cards.MASQUERADE);
		}
		priority.add(Cards.SILVER);
		return priority;
	}

	@Override
	public Set<Card> required() {
		return Collections.singleton(Cards.MASQUERADE);
	}

}
