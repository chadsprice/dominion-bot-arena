package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class BorderVillage extends Card {

    public BorderVillage() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
    }

    @Override
    public void onGain(Player player, Game game) {
        // gain a card costing less than this
        Set<Card> gainable = game.cardsCostingAtMost(Card.BORDER_VILLAGE.cost(game) - 1);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Border Village: Choose a card to gain.");
            game.messageAll("gaining " + toGain.htmlName() + " because of " + Card.BORDER_VILLAGE.htmlNameRaw());
            game.gain(player, toGain);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+2 Actions", "When you gain this, gain a card costing less than this."};
    }

    @Override
    public String toString() {
        return "Border Village";
    }

}
