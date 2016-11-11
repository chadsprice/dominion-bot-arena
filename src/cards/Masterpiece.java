package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class Masterpiece extends Card {

    public Masterpiece() {
        isTreasure = true;
        isOverpayable = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public int treasureValue() {
        return 1;
    }

    @Override
    public void onOverpay(Player player, Game game, int amountOverpaid) {
        // gain a Silver per $1 overpaid
        gain(player, game, Cards.SILVER, amountOverpaid);
    }

    @Override
    public String[] description() {
        return new String[] {"$1", "When you buy this, you may overpay for it. If you do, gain a Silver per $1 you overpaid."};
    }

    @Override
    public String toString() {
        return "Masterpiece";
    }

}
