package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;

public class Feodum extends Card {

    public Feodum() {
        isVictory = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public int victoryValue(List<Card> deck) {
        return (int) deck.stream().filter(c -> c == Cards.SILVER).count() / 3;
    }

    @Override
    public void onTrash(Player player, Game game) {
        // gain 3 Silvers
        int numSilvers = Math.min(3, game.supply.get(Cards.SILVER));
        if (numSilvers != 0) {
            game.messageAll("gaining " + Cards.SILVER.htmlName(numSilvers) + " because of " + this.htmlNameRaw());
            for (int i = 0; i < numSilvers; i++) {
                game.gain(player, Cards.SILVER);
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Worth 1 VP for every 3 Silvers in your deck (round down).", "When you trash this, gain 3 Silvers."};
    }

    @Override
    public String toString() {
        return "Feodum";
    }

}
