package bots;

import server.Bot;
import server.Card;

import java.util.*;

public class AdvisorBM extends Bot {

    @Override
    public String botName() {
        return "AdvisorBM";
    }

    @Override
    public List<Card> gainPriority() {
        List<Card> priority = new ArrayList<>();
        priority.add(Card.PROVINCE);
        if (gainsToEndGame() <= 5) {
            priority.add(Card.DUCHY);
        }
        if (gainsToEndGame() <= 2) {
            priority.add(Card.ESTATE);
        }
        priority.add(Card.GOLD);
        priority.add(Card.ADVISOR);
        priority.add(Card.SILVER);
        if (gainsToEndGame() <= 3) {
            priority.add(Card.COPPER);
        }
        return priority;
    }

    @Override
    public Set<Card> required() {
        return new HashSet<>(Arrays.asList(new Card[] {Card.ADVISOR}));
    }

}
