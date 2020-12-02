package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class FarmingVillage extends Card {

    @Override
    public String name() {
        return "Farming Village";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Actions>",
                "Reveal cards from the_top of your_deck until you_reveal an_Action or Treasure_card. Put that card into your_hand and discard the_other_cards."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 2);
        // reveal cards from the top of your deck until revealing an action or treasure
        // put it into your hand
        revealUntil(
                player,
                game,
                c -> c.isAction() || c.isTreasure(),
                c -> putRevealedIntoHand(player, game, c)
        );
    }

}
