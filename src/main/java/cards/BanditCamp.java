package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class BanditCamp extends Card {

    @Override
    public String name() {
        return "Bandit Camp";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {"<+1_Card>", "<+2_Actions>", "Gain_a_[Spoils] from the_[Spoils]_pile."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
        // gain a Spoils
        if (game.nonSupply.get(Cards.SPOILS) != 0) {
            game.messageAll("gaining " + Cards.SPOILS.htmlName());
            game.gain(player, Cards.SPOILS);
        }
    }

}
