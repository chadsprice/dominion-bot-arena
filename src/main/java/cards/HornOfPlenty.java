package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class HornOfPlenty extends Card {

    @Override
    public String name() {
        return "Horn of Plenty";
    }

    @Override
    public String plural() {
        return "Horns of Plenty";
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "0$",
                "When you play_this, gain a_card costing up_to 1$_per differently_named card you have in_play (including_this).",
                "If it's a_Victory card, trash_this."
        };
    }

    @Override
    public int treasureValue() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        int maxCost = new HashSet<>(player.allCardsInPlay()).size();
        Set<Card> gainable = game.cardsCostingAtMost(maxCost);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": Choose a card to gain (if it's a Victory card, you will trash the " + this.htmlNameRaw() + ")"
            );
            game.messageAll("gaining " + toGain.htmlName());
            // if the gained card was replaced, do not trash this
            boolean replaced = game.gain(player, toGain);
            if (toGain.isVictory() && !replaced) {
                game.messageAll("trashing the " + this.htmlNameRaw());
                player.removeFromPlay(this);
                game.trash(player, this);
            }
        } else {
            game.messageAll("gaining nothing");
        }
    }

}
