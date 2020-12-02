package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class RuinedLibrary extends Card {

    @Override
    public String name() {
        return "Ruined Library";
    }

    @Override
    public String plural() {
        return "Ruined Libraries";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.RUINS);
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {"<+1_Card>"};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
    }

}
