package bots;

import server.Bot;
import server.Card;

import java.util.*;

public class BigSmithy extends Bot {

    @Override
    public String botName() {
        return "BigSmithy";
    }

    @Override
    public List<Card> gainPriority() {
        List<Card> priority = new ArrayList<Card>();
        if (countInDeck(Card.PLATINUM) > 0) {
            priority.add(Card.COLONY);
        }
        if (countInSupply(Card.COLONY) <= 6 || countInSupply(Card.PROVINCE) <= 6) {
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
        if (countInDeck(Card.SMITHY) < 2 && getDeck().size() >= 16) {
            priority.add(Card.SMITHY);
        }
        priority.add(Card.SILVER);
        if (gainsToEndGame() <= 3) {
            priority.add(Card.COPPER);
        }
        return priority;
    }

    @Override
    public Set<Card> required() {
        return new HashSet<Card>(Arrays.asList(new Card[] {Card.SMITHY}));
    }
}
