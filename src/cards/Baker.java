package cards;

import server.Card;
import server.Game;
import server.Player;

public class Baker extends Card {

    public Baker() {
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
        plusCoinTokens(player, game, 1);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Take a Coin token.", "Setup: Each player takes a Coin token."};
    }

    @Override
    public String toString() {
        return "Baker";
    }

}
