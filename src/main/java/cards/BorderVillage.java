package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class BorderVillage extends Card {

    @Override
    public String name() {
        return "Border Village";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+2_Actions>",
                "When you gain_this, gain_a_card costing less_than_this."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 2);
    }

    @Override
    public void onGain(Player player, Game game) {
        // gain a card costing less than this
        Set<Card> gainable = game.cardsCostingAtMost(Cards.BORDER_VILLAGE.cost(game) - 1);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                player,
                game,
                gainable,
                this.toString() + ": Choose a card to gain."
            );
            game.messageAll("gaining " + toGain.htmlName() + " because of " + Cards.BORDER_VILLAGE.htmlNameRaw());
            game.gain(player, toGain);
        }
    }

}
