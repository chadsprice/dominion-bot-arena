package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class SirMichael extends Knight {

    public SirMichael() {
        super();
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // other players discard down to 3
        targets.forEach(target -> {
            if (target.getHand().size() > 3) {
                int count = target.getHand().size() - 3;
                List<Card> discarded = game.promptDiscardNumber(target, count, "Sir Michael", "attackPrompt");
                game.message(target, "You discard " + Card.htmlList(discarded));
                game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
                target.putFromHandIntoDiscard(discarded);
            }
        });
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "Each other player discards down to 3 cards in hand.");
    }

    @Override
    public String toString() {
        return "Sir Michael";
    }

}
