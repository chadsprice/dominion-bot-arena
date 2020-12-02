package bots;

import server.Bot;
import server.Card;
import server.Cards;

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
            priority.add(Cards.PROVINCE);
        }
        if (gainsToEndGame() <= 5) {
            priority.add(Cards.DUCHY);
        }
        if (gainsToEndGame() <= 5) {
            priority.add(Cards.ESTATE);
        }
        priority.add(Cards.GOLD);
        if (countInDeck(Cards.JACK_OF_ALL_TRADES) < 2) {
            priority.add(Cards.JACK_OF_ALL_TRADES);
        }
        priority.add(Cards.SILVER);
        return priority;
    }

    @Override
    public Set<Card> required() {
        return Collections.singleton(Cards.JACK_OF_ALL_TRADES);
    }

}
