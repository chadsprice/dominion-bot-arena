package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Margrave extends Card {

    @Override
    public String name() {
        return "Margrave";
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
        return new String[] {
                "<+3_Cards>",
                "<+1_Buy>",
                "Each other_player draws a_card , then discards down_to 3_cards in_hand."
        };
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 3);
        plusBuys(player, game, 1);
        for (Player target : targets) {
            // draw 1 card
            List<Card> drawn = target.drawIntoHand(1);
            game.message(target, "You draw " + Card.htmlList(drawn));
            game.messageOpponents(target, target.username + " draws " + Card.numCards(drawn.size()));
            // discard down to 3 in hand
            if (target.getHand().size() > 3) {
                int count = target.getHand().size() - 3;
                List<Card> discarded = promptDiscardNumber(
                        target,
                        game,
                        count
                );
                game.message(target, "You discard " + Card.htmlList(discarded));
                game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
                target.putFromHandIntoDiscard(discarded);
            }
        }
    }

}
