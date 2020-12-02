package bots;

import server.Bot;
import server.Card;
import server.Cards;

import java.util.*;

public class BigSmithy extends Bot {

    @Override
    public String botName() {
        return "BigSmithy";
    }

    @Override
    public List<Card> gainPriority() {
        List<Card> priority = new ArrayList<>();
        if (countInDeck(Cards.PLATINUM) > 0) {
            priority.add(Cards.COLONY);
        }
        if (countInSupply(Cards.COLONY) <= 6 || countInSupply(Cards.PROVINCE) <= 6) {
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
        if (countInDeck(Cards.SMITHY) < 2 && getDeck().size() >= 16) {
            priority.add(Cards.SMITHY);
        }
        priority.add(Cards.SILVER);
        if (gainsToEndGame() <= 3) {
            priority.add(Cards.COPPER);
        }
        return priority;
    }

    @Override
    public Set<Card> required() {
        return Collections.singleton(Cards.SMITHY);
    }
}
