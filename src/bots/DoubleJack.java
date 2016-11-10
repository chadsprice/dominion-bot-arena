package bots;

import server.Bot;
import server.Card;

import java.util.*;

public class DoubleJack extends Bot {

    @Override
    public String botName() {
        return "DoubleJack";
    }

    @Override
    public List<Card> gainPriority() {
        // based on rspeer's "DoubleJack" bot
        List<Card> priority = new ArrayList<>();
        if (getTotalMoney() > 15) {
            priority.add(Card.PROVINCE);
        }
        if (gainsToEndGame() <= 5) {
            priority.add(Card.DUCHY);
        }
        if (gainsToEndGame() <= 5) {
            priority.add(Card.ESTATE);
        }
        priority.add(Card.GOLD);
        if (countInDeck(Card.JACK_OF_ALL_TRADES) < 2) {
            priority.add(Card.JACK_OF_ALL_TRADES);
        }
        priority.add(Card.SILVER);
        return priority;
    }

    @Override
    public Set<Card> required() {
        return Collections.singleton(Card.JACK_OF_ALL_TRADES);
    }

}
