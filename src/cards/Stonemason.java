package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Stonemason extends Card {

    public Stonemason() {
        isAction = true;
        isOverpayable = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // trash a card from your hand
        if (!player.getHand().isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), this.toString() + ": Choose a card to trash and gain 2 cards each costing less than it.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain 2 cards each costing less than it
            int maxCost = toTrash.cost(game) - 1;
            for (int i = 0; i < 2; i++) {
                Set<Card> gainable = game.cardsCostingAtMost(maxCost);
                if (!gainable.isEmpty()) {
                    Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain.");
                    game.messageAll("gaining " + toGain.htmlName());
                    game.gain(player, toGain);
                } else {
                    game.messageAll("gaining nothing");
                    break;
                }
            }
        } else {
            game.messageAll("having no card in hand to trash");
        }
    }

    @Override
    public void onOverpay(Player player, Game game, int amoundOverpaid) {
        // gain 2 action cards each costing the amount you overpayed
        for (int i = 0; i < 2; i++) {
            Set<Card> gainable = game.cardsCostingExactly(amoundOverpaid).stream()
                    .filter(c -> c.isAction)
                    .collect(Collectors.toSet());
            if (!gainable.isEmpty()) {
                Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain.");
                game.messageAll("gaining " + toGain.htmlName());
                game.gain(player, toGain);
            } else {
                game.messageAll("gaining nothing");
                break;
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Trash a card from your hand.", "Gain 2 cards each costing less than it.", "When you buy this, you may overpay for it.", "If you do, gain 2 Action cards each costing the amount you overpaid."};
    }

    @Override
    public String toString() {
        return "Stonemason";
    }

}
