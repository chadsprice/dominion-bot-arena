package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeathCart extends Card {

    public DeathCart() {
        isAction = true;
        isLooter = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        plusCoins(player, game, 5);
        // you may trash an action, if you don't, then trash this from play
        boolean trashingThis = true;
        Set<Card> trashable = player.getHand().stream().filter(c -> c.isAction).collect(Collectors.toSet());
        if (!trashable.isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, trashable, "Death Cart: You may trash an card from your hand, or", false, hasMoved ? "Trash nothing" : "Trash this");
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.addToTrash(player, toTrash);
                trashingThis = false;
            }
        }
        // this can't be trashed from play if it has already been moved
        if (trashingThis && !hasMoved) {
            game.messageAll("trashing the " + this.htmlNameRaw());
            player.removeFromPlay(this);
            game.addToTrash(player, this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onGain(Player player, Game game) {
        // gain 2 Ruins
        if (game.mixedPiles.containsKey(MixedPileId.RUINS)) {
            List<Card> ruins = game.mixedPiles.get(MixedPileId.RUINS);
            if (!ruins.isEmpty()) {
                List<Card> toGain = ruins.subList(0, Math.min(2, ruins.size()));
                game.messageAll("gaining " + Card.htmlList(toGain));
                for (int i = 0; i < 2 && !game.mixedPiles.get(MixedPileId.RUINS).isEmpty(); i++) {
                    game.gain(player, game.mixedPiles.get(MixedPileId.RUINS).get(0));
                }
            }
        }
    }

    @Override
    public String htmlType() {
        return "Action-Looter";
    }

    @Override
    public String[] description() {
        return new String[] {"+$5", "You may trash an Action card from your hand. If you don't, trash this.", "When you gain this, gain 2 Ruins."};
    }

    @Override
    public String toString() {
        return "Death Cart";
    }

}
