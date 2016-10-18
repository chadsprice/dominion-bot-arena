package cards;

import server.Card;
import server.Game;
import server.Player;

public class FarmingVillage extends Card {

    public FarmingVillage() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 2);
        // reveal cards from the top of your deck until revealing an action or treasure
        // put it into your hand
        revealUntil(player, game,
                c -> c.isAction || c.isTreasure,
                c -> putRevealedIntoHand(player, game, c));
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Actions", "Reveal cards from the top of your deck until you reveal an Action or Treasure card. Put that card into your hand and discard the other cards."};
    }

    @Override
    public String toString() {
        return "Farming Village";
    }

}
