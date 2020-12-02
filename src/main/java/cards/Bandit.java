package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.*;

public class Bandit extends Card {

    @Override
    public String name() {
        return "Bandit";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a_[Gold]. Each_other_player reveals the_top_2_cards of_their_deck, trashes a_revealed_Treasure other_than_[Copper], and discards_the_rest."};
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // gain a Gold
        gain(player, game, Cards.GOLD);
        // each other player reveals the top 2 cards of their deck, then trashes a non-Copper treasure
        topTwoCardsAttack(
                targets,
                game,
                c -> c.isTreasure() && c != Cards.COPPER
        );
    }

}
