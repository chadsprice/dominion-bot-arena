package bots;

import server.Bot;
import server.Card;

import java.util.Collections;
import java.util.List;

public class BigNothing extends Bot {

    @Override
    public String botName() {
        return "BigNothing";
    }

    @Override
    public List<Card> gainPriority() {
        return Collections.emptyList();
    }

}
