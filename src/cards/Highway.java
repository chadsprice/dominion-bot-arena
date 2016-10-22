package cards;

import server.Card;
import server.Game;
import server.Player;

public class Highway extends Card {

    public Highway() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        game.messageAll("cards cost $1 less while this is in play");
        game.costModifierPlayedLastTurn = true;
        game.sendCardCosts();
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "While this is in play, cards cost $1 less, but not less than $0."};
    }

    @Override
    public String toString() {
        return "Highway";
    }

}
