package cards;

import server.Card;
import server.Game;
import server.Player;

public class BanditCamp extends Card {

    public BanditCamp() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
        // gain a Spoils
        if (game.nonSupply.get(Card.SPOILS) != 0) {
            game.messageAll("gaining " + Card.SPOILS.htmlName());
            game.gain(player, Card.SPOILS);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+2 Actions", "Gain a Spoils from the Spoils pile."};
    }

    @Override
    public String toString() {
        return "Bandit Camp";
    }

}
