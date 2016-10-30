package cards;

import server.Card;
import server.Game;
import server.Player;

public class Mystic extends Card {

    public Mystic() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        plusCoins(player, game, 2);
        tryToNameTopCardOfDeck(player, game, "Mystic");
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "+$2", "Name a card.", "Reveal the top card of your deck.", "If it's the named card, put it into your hand."};
    }

    @Override
    public String toString() {
        return "Mystic";
    }

}
