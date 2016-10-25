package cards;

import server.Card;
import server.Game;
import server.Player;

public class Beggar extends Card {

    public Beggar() {
        isAction = true;
        isAttackReaction = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        int numCoppers = Math.min(3, game.supply.get(Card.COPPER));
        game.message(player, "gaining " + Card.COPPER.htmlName(numCoppers) + " to your hand");
        game.messageOpponents(player, "gaining " + Card.COPPER.htmlName(numCoppers) + " to their hand");
        for (int i = 0; i < numCoppers; i++) {
            game.gainToHand(player, Card.COPPER);
        }
    }

    @Override
    public boolean onAttackReaction(Player player, Game game) {
        int numSilvers = game.supply.get(Card.SILVER);
        game.message(player, reactionStr(numSilvers, true));
        game.messageOpponents(player, reactionStr(numSilvers, false));
        // discard this Beggar
        player.putFromHandIntoDiscard(this);
        if (game.supply.get(Card.SILVER) != 0) {
            // gain the first Silver onto your deck
            game.gainToTopOfDeck(player, Card.SILVER);
            if (game.supply.get(Card.SILVER) != 0) {
                // gain the second to your discard
                game.gain(player, Card.SILVER);
            }
        }
        return false;
    }

    private String reactionStr(int numSilvers, boolean isPlayer) {
        String str = "discarding it";
        if (numSilvers > 0) {
            str += " and gaining " + Card.SILVER.htmlName(numSilvers) + ", putting " + ((numSilvers == 1) ? "it" : "one") + " on top of " + (isPlayer ? "your" : "their") + " deck";
        }
        return str;
    }

    @Override
    public String[] description() {
        return new String[] {"Gain 3 Coppers, putting them into your hand.", "When another player plays an Attack card, you may discard this. If you do, gain 2 Silvers, putting one on top of your deck."};
    }

    @Override
    public String toString() {
        return "Beggar";
    }

}
