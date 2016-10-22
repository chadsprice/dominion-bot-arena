package cards;

import server.Card;
import server.Game;
import server.Player;

public class Haggler extends Card {

    public Haggler() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
    }

    @Override
    public String[] description() {
        return new String[] {"+$2", "While this is in play, when you buy a card, gain a card costing less than it that is not a Victory card."};
    }

    @Override
    public String toString() {
        return "Haggler";
    }

}
