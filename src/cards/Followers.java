package cards;

import server.Card;
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
        // gain an estate
        if (game.supply.get(Card.ESTATE) != 0) {
            game.messageAll("gaining " + Card.ESTATE.htmlName());
            game.gain(player, Card.ESTATE);
        }
        // each other player gains a curse and discards down to 3 in hand
        for (Player target : targets) {
            if (game.supply.get(Card.CURSE) != 0) {
                game.message(target, "You gain " + Card.CURSE.htmlName());
                game.messageOpponents(target, target.username + " gains " + Card.CURSE.htmlName());
                game.gain(target, Card.CURSE);
            }
            if (target.getHand().size() > 3) {
                int count = target.getHand().size() - 3;
                List<Card> discarded = game.promptDiscardNumber(target, count, "Followers", "attackPrompt");
                target.putFromHandIntoDiscard(discarded);
                game.message(target, "You discard " + Card.htmlList(discarded));
                game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
            }
        }
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
