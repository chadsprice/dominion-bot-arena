package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Marauder extends Card {

    public Marauder() {
        isAction = true;
        isAttack = true;
        isLooter = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // gain a Spoils
        if (game.nonSupply.get(Card.SPOILS) != 0) {
            game.messageAll("gaining " + Card.SPOILS.htmlName());
            game.gain(player, Card.SPOILS);
        }
        // each other player gains a ruins
        targets.forEach(target -> {
            if (!game.mixedPiles.get(MixedPileId.RUINS).isEmpty()) {
                Card ruins = game.mixedPiles.get(MixedPileId.RUINS).get(0);
                game.message(target, "You gain " + ruins.htmlName());
                game.messageOpponents(target, target.username + " gains " + ruins.htmlName());
                game.gain(target, ruins);
            }
        });
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Looter";
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a Spoils from the Spoils pile.", "Each other player gains a Ruins."};
    }

    @Override
    public String toString() {
        return "Marauder";
    }

}
