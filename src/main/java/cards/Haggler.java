package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Haggler extends Card {

    @Override
    public String name() {
        return "Haggler";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2$>",
                "While this is in_play, when you buy a_card, gain a_card costing less_than_it that is not a_Victory_card."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
    }

    public Card chooseGain(Player player, Game game, Set<Card> gainable) {
        return promptChooseGainFromSupply(
                player,
                game,
                gainable,
                this.toString() + ": Choose a card to gain."
        );
    }

}
