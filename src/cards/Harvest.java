package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.List;

public class Harvest extends Card {

    public Harvest() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        List<Card> revealed = player.takeFromDraw(4);
        if (!revealed.isEmpty()) {
            game.messageAll("revealing and discarding " + Card.htmlList(revealed));
            player.addToDiscard(revealed);
            int numDifferentlyNamed = new HashSet<>(revealed).size();
            plusCoins(player, game, numDifferentlyNamed);
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Reveal the top 4 cards of your deck, then discard them, +$1 per differently named card revealed."};
    }

    @Override
    public String toString() {
        return "Harvest";
    }

}
