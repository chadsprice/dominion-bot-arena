package cards;

import server.Card;
import server.Game;
import server.Player;

public class RuinedLibrary extends Card {

    public RuinedLibrary() {
        isAction = true;
        isRuins = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card"};
    }

    @Override
    public String toString() {
        return "Ruined Library";
    }

    @Override
    public String plural() {
        return "Ruined Libraries";
    }

}
