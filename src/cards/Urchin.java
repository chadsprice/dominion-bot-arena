package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Urchin extends Card {

    public Urchin() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // each other player discards down to 4 cards in hand
        targets.forEach(target -> {
            if (target.getHand().size() > 4) {
                int count = target.getHand().size() - 4;
                List<Card> discarded = game.promptDiscardNumber(target, count, "Urchin", "attackPrompt");
                game.message(target, "You discard " + Card.htmlList(discarded));
                game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
                target.putFromHandIntoDiscard(discarded);
            }
        });
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Each other player discards down to 4 cards in hand.", "When you play another Attack card with this in play, you may trash this. If you do, gain a Mercenary from the Mercenary pile."};
    }

    @Override
    public String toString() {
        return "Urchin";
    }

}
