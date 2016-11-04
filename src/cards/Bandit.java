package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.*;

public class Bandit extends Card {

    public Bandit() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // gain a Gold
        gain(player, game, Card.GOLD);
        // each other player reveals the top 2 cards of their deck, then trashes a non-Copper treasure
        topTwoCardsAttack(targets, game,
                c -> c.isTreasure && c != Card.COPPER);
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a Gold. Each other player reveals the top 2 cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest."};
    }

    @Override
    public String toString() {
        return "Bandit";
    }

}
