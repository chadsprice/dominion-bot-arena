package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Stonemason extends Card {

    @Override
    public String name() {
        return "Stonemason";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public boolean isOverpayable() {
        return true;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Trash a_card from your_hand.",
                "Gain 2_cards each costing less than_it.",
                "When you buy_this, you_may overpay for_it.",
                "If_you_do, gain 2_Action cards each costing the_amount you_overpaid."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        // trash a card from your hand
        if (!player.getHand().isEmpty()) {
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": Choose a card to trash and gain 2 cards each costing less than it."
            );
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain 2 cards each costing less than it
            int maxCost = toTrash.cost(game) - 1;
            for (int i = 0; i < 2; i++) {
                Set<Card> gainable = game.cardsCostingAtMost(maxCost);
                if (!gainable.isEmpty()) {
                    Card toGain = promptChooseGainFromSupply(
                            player,
                            game,
                            gainable,
                            this.toString() + ": Choose a card to gain."
                    );
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
                    .filter(Card::isAction)
                    .collect(Collectors.toSet());
            if (!gainable.isEmpty()) {
                Card toGain = promptChooseGainFromSupply(
                        player,
                        game,
                        gainable,
                        this.toString() + ": Choose a card to gain."
                );
                game.messageAll("gaining " + toGain.htmlName());
                game.gain(player, toGain);
            } else {
                game.messageAll("gaining nothing");
                break;
            }
        }
    }

}
