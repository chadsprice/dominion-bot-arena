package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeathCart extends Card {

    @Override
    public String name() {
        return "Death Cart";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.LOOTER);
    }

    @Override
    public String htmlType() {
        return "Action-Looter";
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {"<+$5>", "You may trash an_Action card from your_hand. If you_don't, trash_this.", "When you gain_this, gain 2_Ruins."};
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        plusCoins(player, game, 5);
        // you may trash an action, if you don't, then trash this from play
        boolean trashingThis = true;
        Set<Card> trashable = player.getHand().stream()
                .filter(Card::isAction)
                .collect(Collectors.toSet());
        if (!trashable.isEmpty()) {
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    trashable,
                    this.toString() + ": You may trash an card from your hand, or",
                    hasMoved ? "Trash nothing" : "Trash this"
            );
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                trashingThis = false;
            }
        }
        // this can't be trashed from play if it has already been moved
        if (trashingThis && !hasMoved) {
            game.messageAll("trashing the " + this.htmlNameRaw());
            player.removeFromPlay(this);
            game.trash(player, this);
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
                game.messageAll("gaining " + Card.htmlList(toGain) + " because of " + this.htmlNameRaw());
                for (int i = 0; i < 2 && !game.mixedPiles.get(MixedPileId.RUINS).isEmpty(); i++) {
                    game.gain(player, game.mixedPiles.get(MixedPileId.RUINS).get(0));
                }
            }
        }
    }

}
