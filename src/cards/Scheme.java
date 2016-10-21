package cards;

import server.Card;
import server.Game;
import server.Player;

public class Scheme extends Card {

    public Scheme() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        game.schemesPlayedThisTurn++;
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "At the start of Clean-up this turn, you may choose an Action card you have in play. If you discard it from play this turn, put it on your deck."};
    }

    @Override
    public String toString() {
        return "Scheme";
    }

}
