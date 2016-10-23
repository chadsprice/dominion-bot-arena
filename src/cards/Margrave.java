package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Margrave extends Card {

    public Margrave() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 5;
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
                List<Card> discarded = game.promptDiscardNumber(target, count, "Militia", "attackPrompt");
                game.message(target, "You discard " + Card.htmlList(discarded));
                game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
                target.putFromHandIntoDiscard(discarded);
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+3 Cards", "+1 Buy", "Each other player draws a card , then discards down to 3 cards in hand."};
    }

    @Override
    public String toString() {
        return "Margrave";
    }

}
