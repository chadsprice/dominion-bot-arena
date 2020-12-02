package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class Fortress extends Card {

    @Override
    public String name() {
        return "Fortress";
    }

    @Override
    public String plural() {
        return "Fortresses";
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
                "<+1_Card>",
                "<+2_Actions>",
                "When you trash_this, put_it into your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
    }

    @Override
    public boolean onTrashIsTrashed(Player player, Game game) {
        game.message(player, "putting the " + this.htmlNameRaw() + " into your hand");
        game.messageOpponents(player, "putting the " + this.htmlNameRaw() + " into their hand");
        player.addToHand(isBandOfMisfits ? Cards.BAND_OF_MISFITS : this);
        return false;
    }

}
