package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class Masterpiece extends Card {

    @Override
    public String name() {
        return "Masterpiece";
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public boolean isOverpayable() {
        return true;
    }

    @Override
    public String[] description() {
        return new String[] {
                "1$",
                "When you buy_this, you_may overpay for_it. If_you_do, gain a_[Silver] per_1$ you_overpaid."
        };
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

}
