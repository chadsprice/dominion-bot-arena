package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Marauder extends Card {

    @Override
    public String name() {
        return "Marauder";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK, Type.LOOTER);
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Looter";
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Gain a_[Spoils] from the [Spoils]_pile.",
                "Each other_player gains a_Ruins."
        };
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // gain a Spoils
        if (game.nonSupply.get(Cards.SPOILS) != 0) {
            game.messageAll("gaining " + Cards.SPOILS.htmlName());
            game.gain(player, Cards.SPOILS);
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

}
