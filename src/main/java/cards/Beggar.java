package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class Beggar extends Card {

    @Override
    public String name() {
        return "Beggar";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK_REACTION);
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {"Gain_3_[Coppers], putting_them into_your_hand.", "When another_player plays an_Attack_card, you may discard_this. If_you_do, gain_2_[Silvers], putting_one on_top_of your_deck."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        int numCoppers = Math.min(3, game.supply.get(Cards.COPPER));
        game.message(player, "gaining " + Cards.COPPER.htmlName(numCoppers) + " to your hand");
        game.messageOpponents(player, "gaining " + Cards.COPPER.htmlName(numCoppers) + " to their hand");
        for (int i = 0; i < numCoppers; i++) {
            game.gainToHand(player, Cards.COPPER);
        }
    }

    @Override
    public boolean onAttackReaction(Player player, Game game) {
        game.message(player, reactionStr(game, true));
        game.messageOpponents(player, reactionStr(game, false));
        // discard this Beggar
        player.putFromHandIntoDiscard(this);
        if (game.supply.get(Cards.SILVER) != 0) {
            // gain the first Silver onto your deck
            game.gainToTopOfDeck(player, Cards.SILVER);
            if (game.supply.get(Cards.SILVER) != 0) {
                // gain the second to your discard
                game.gain(player, Cards.SILVER);
            }
        }
        return false;
    }

    private String reactionStr(Game game, boolean isPlayer) {
        String str = "discarding it";
        if (game.supply.get(Cards.SILVER) == 1) {
            str += " and gaining " + Cards.SILVER.htmlName() + ", putting it on top of " + (isPlayer ? "your" : "their") + " deck";
        } else if (game.supply.get(Cards.SILVER) >= 2) {
            str += " and gaining " + Cards.SILVER.htmlName(2) + ", putting one on top of " + (isPlayer ? "your" : "their") + " deck";
        }
        return str;
    }

}
