package cards;

import server.Card;
import server.Game;
import server.Player;

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
    public String[] description() {
        return new String[] {"+1 Card", "+2 Actions", "When you gain this, gain a card costing less than this."};
    }

    @Override
    public String toString() {
        return "Border Village";
    }

}
