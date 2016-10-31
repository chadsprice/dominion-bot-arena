package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class DameNatalie extends Knight {

    public DameNatalie() {
        super();
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // you may gain a card costing up to $3
        Set<Card> gainable = game.cardsCostingAtMost(3);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Dame Natalie: You may gain a card costing up to $3.", false, "Gain nothing");
            if (toGain != null) {
                game.messageAll("gaining " + toGain.htmlName());
                game.gain(player, toGain);
            }
        }
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "You may gain a card costing up to $3.");
    }

    @Override
    public String toString() {
        return "Dame Natalie";
    }

}
