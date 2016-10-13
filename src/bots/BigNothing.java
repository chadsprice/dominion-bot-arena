package bots;

import server.Bot;
import server.Card;

import java.util.ArrayList;
import java.util.List;

public class BigNothing extends Bot {

    @Override
    public String botName() {
        return "BigNothing";
    }

    @Override
    public List<Card> gainPriority() {
        return new ArrayList<Card>();
    }

}
