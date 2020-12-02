package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Harvest extends Card {

    @Override
    public String name() {
        return "Harvest";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {"Reveal the_top 4 cards of your_deck, then discard them, <+1$>_per differently_named card_revealed."};
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

}
