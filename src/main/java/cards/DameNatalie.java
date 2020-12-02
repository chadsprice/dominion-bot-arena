package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class DameNatalie extends Knight {

    @Override
    public String name() {
        return "Dame Natalie";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add(0, "You may gain_a_card costing up_to 3$.");
    }

    @Override
    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // you may gain a card costing up to $3
        Set<Card> gainable = game.cardsCostingAtMost(3);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": You may gain a card costing up to $3.",
                    "Gain Nothing"
            );
            if (toGain != null) {
                game.messageAll("gaining " + toGain.htmlName());
                game.gain(player, toGain);
            }
        }
    }

}
