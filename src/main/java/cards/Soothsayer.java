package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Soothsayer extends Card {

    @Override
    public String name() {
        return "Soothsayer";
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
        return new String[] {"Gain a_[Gold]. Each other_player gains a_[Curse]. Each_player who did draws_a_card."};
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // gain a Gold
        gain(player, game, Cards.GOLD);
        // each other player gains a Curse
        targets.forEach(target -> {
            if (game.isAvailableInSupply(Cards.CURSE)) {
                game.message(target, "You gain " + Cards.CURSE.htmlName());
                game.messageOpponents(target, target.username + " gains " + Cards.CURSE.htmlName());
                game.gain(target, Cards.CURSE);
                // each player who gain a Curse draws a card
                game.messageIndent++;
                plusCards(target, game, 1);
                game.messageIndent--;
            }
        });
    }

}
