package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class HornOfPlenty extends Card {

    public HornOfPlenty() {
        isTreasure = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public int treasureValue() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        int maxCost = new HashSet<Card>(player.allCardsInPlay()).size();
        Set<Card> gainable = game.cardsCostingAtMost(maxCost);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Horn of Plenty: Choose a card to gain (if it's a Victory card, you will trash the " + this.htmlNameRaw() + ")");
            game.messageAll("gaining " + toGain.htmlName());
            // if the gained card was replaced, do not trash this
            boolean replaced = game.gain(player, toGain);
            if (toGain.isVictory && !replaced) {
                game.messageAll("trashing the " + this.htmlNameRaw());
                player.removeFromPlay(this);
                game.trash(player, this);
            }
        } else {
            game.messageAll("gaining nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"$0", "When you play this, gain a card costing up to $1 per differently named card you have in play, counting this.", "If it's a Victory card, trash this."};
    }

    @Override
    public String toString() {
        return "Horn of Plenty";
    }

    @Override
    public String plural() {
        return "Horns of Plenty";
    }

}
