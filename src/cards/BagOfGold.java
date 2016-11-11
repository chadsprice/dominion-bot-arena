package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class BagOfGold extends Card {

    public BagOfGold() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        // gain a gold onto your deck
        gainOntoDeck(player, game, Cards.GOLD);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "Gain a Gold, putting it on top of your deck.", "(This is not in the Supply.)"};
    }

    @Override
    public String toString() {
        return "Bag of Gold";
    }

    @Override
    public String htmlType() {
        return "Action-Prize";
    }

}
