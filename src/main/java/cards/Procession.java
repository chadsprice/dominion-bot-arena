package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class Procession extends Card {

    @Override
    public String name() {
        return "Procession";
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
        return new String[] {"You may play an_Action card from your_hand twice. Trash_it. Gain an_Action card costing exactly 1$_more than_it."};
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        return onThroneRoomVariant(player, game, 2, false, hasMoved);
    }

    @Override
    protected void afterThroneRoomVariant(Player player, Game game, Card played, boolean playedMoved) {
        // trash the played card
        if (playedMoved && (played.isDuration() || played instanceof ThroneRoom || played instanceof KingsCourt || played instanceof Procession)) {
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
        int cost = played.cost(game);
        if (played.isBandOfMisfits) {
            // if the card was a Band of Misfits imitator, use the cost of the Band of Misfits
            cost = Cards.BAND_OF_MISFITS.cost(game);
        }
        Set<Card> gainable = game.cardsCostingExactly(cost + 1);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": Choose a card to gain."
            );
            game.messageAll("gaining " + toGain.htmlName());
            game.gain(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
    }

}
