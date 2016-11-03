package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Procession extends Card {

    public Procession() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        return onThroneRoomVariant(player, game, 2, false, hasMoved);
    }

    @Override
    protected void afterThroneRoomVariant(Player player, Game game, Card played, boolean playedMoved) {
        // trash the played card
        if (played.isDuration && playedMoved) {
            // remove it from the set aside duration cards
            game.messageAll("trashing the " + played.htmlNameRaw());
            player.removeDurationSetAside(played);
            game.trash(player, played);
        } else if (!playedMoved) {
            // remove it from play
            game.messageAll("trashing the " + played.htmlNameRaw());
            player.removeFromPlay(played);
            game.trash(player, played);
        }
        // gain a card costing exactly $1 more than the trashed card
        Set<Card> gainable = game.cardsCostingExactly(played.cost(game) + 1);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Procession: Choose a card to gain.");
            game.messageAll("gaining " + toGain.htmlName());
            game.gain(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"You may play an Action card from you hand twice. Trash it. Gain an Action card costing exactly $1 more than it."};
    }

    @Override
    public String toString() {
        return "Procession";
    }

}
