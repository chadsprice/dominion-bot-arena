package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Urchin extends Card {

    @Override
    public String name() {
        return "Urchin";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "Each other player discards down_to 4_cards in_hand.",
                "When you play another Attack card with this in_play, you_may trash_this. If_you_do, gain a_[Mercenary] from the [Mercenary]_pile."
        };
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // each other player discards down to 4 cards in hand
        handSizeAttack(targets, game, 4);
    }

}
