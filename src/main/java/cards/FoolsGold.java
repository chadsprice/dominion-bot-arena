package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class FoolsGold extends Card {

    @Override
    public String name() {
        return "Fool's Gold";
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public String htmlType() {
        return "Treasure-Reaction";
    }

    @Override
    public String htmlHighlightType() {
        return "treasure-reaction";
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {
                "If this_is the_first time you played a_[Fool's Gold] this_turn, this is worth_1$, otherwise it's_worth_4$.",
                "When another player gains a Province, you may trash this from your hand.",
                "If_you_do, gain a_[Gold], putting_it on your_deck."
        };
    }

    @Override
    public int treasureValue(Game game) {
        if (!game.currentPlayer().playedFoolsGoldThisTurn) {
            return 1;
        } else {
            return 4;
        }
    }

    @Override
    public void onPlay(Player player, Game game) {
        player.playedFoolsGoldThisTurn = true;
    }

}
