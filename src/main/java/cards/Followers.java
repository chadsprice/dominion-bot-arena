package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Followers extends Card {

    @Override
    public String name() {
        return "Followers";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Prize";
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Cards>",
                "Gain an_[Estate]. Each other_player gains a_[Curse] and discards down_to 3_cards in_hand.", "(This_is_not in the_Supply.)"
        };
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 2);
        // gain an Estate
        gain(player, game, Cards.ESTATE);
        // each other player gains a curse and discards down to 3 in hand
        junkingAttack(targets, game, Cards.CURSE);
        handSizeAttack(targets, game, 3);
    }

}
