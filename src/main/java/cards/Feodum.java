package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Feodum extends Card {

    @Override
    public String name() {
        return "Feodum";
    }

    @Override
    public Set<Type> types() {
        return types(Type.VICTORY);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Worth 1_VP for_every 3_[Silvers] in your_deck (rounded_down).",
                "When you trash_this, gain 3_[Silvers]."
        };
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

}
