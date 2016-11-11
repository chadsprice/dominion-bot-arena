package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;

public class Soothsayer extends Card {

    public Soothsayer() {
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

    @Override
    public String[] description() {
        return new String[] {"Gain a Gold. Each other player gains a Curse. Each player who did draws a card."};
    }

    @Override
    public String toString() {
        return "Soothsayer";
    }

}
