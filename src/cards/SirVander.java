package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class SirVander extends Knight {

    public SirVander() {
        super();
    }

    @Override
    public void onTrash(Player player, Game game) {
        if (game.supply.get(Card.GOLD) != 0) {
            game.messageAll("gaining " + Card.GOLD.htmlName());
            game.gain(player, Card.GOLD);
        }
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add("When you trash this, gain a Gold.");
    }

    @Override
    public String toString() {
        return "Sir Vander";
    }

}
