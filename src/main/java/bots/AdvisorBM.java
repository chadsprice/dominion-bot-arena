package bots;

import server.Bot;
import server.Card;
import server.Cards;

import java.util.*;

public class AdvisorBM extends Bot {

    @Override
    public String botName() {
        return "AdvisorBM";
    }

    @Override
    public List<Card> gainPriority() {
        List<Card> priority = new ArrayList<>();
        priority.add(Cards.PROVINCE);
        if (gainsToEndGame() <= 5) {
            priority.add(Cards.DUCHY);
        }
        if (gainsToEndGame() <= 2) {
            priority.add(Cards.ESTATE);
        }
        priority.add(Cards.GOLD);
        priority.add(Cards.ADVISOR);
        priority.add(Cards.SILVER);
        if (gainsToEndGame() <= 3) {
            priority.add(Cards.COPPER);
        }
        return priority;
    }

    @Override
    public Set<Card> required() {
        return Collections.singleton(Cards.ADVISOR);
    }

}
