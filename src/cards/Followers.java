package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;

public class Followers extends Card {

    public Followers() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 0;
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

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "Gain an Estate. Each other player gains a Curse and discards down to 3 cards in hand.", "(This is not in the Supply.)"};
    }

    @Override
    public String toString() {
        return "Followers";
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Prize";
    }

}
