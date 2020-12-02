package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Diplomat extends Card {

    @Override
    public String name() {
        return "Diplomat";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK_REACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Cards>",
                "If you have 5_or_fewer cards in_hand (after_drawing), <+2_Actions>.",
                "When another_player plays an_Attack card, you may_first reveal_this from a_hand of 5_or_more cards, to draw 2_cards then discard_3."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        if (player.getHand().size() <= 5) {
            plusActions(player, game, 2);
        }
    }

    @Override
    public boolean onAttackReaction(Player player, Game game) {
        plusCards(player, game, 2);
        discardNumber(player, game, 3);
        return false;
    }

}
