package cards;

import server.*;

import java.util.HashSet;
import java.util.Set;

public class Butcher extends Card {

    @Override
    public String name() {
        return "Butcher";
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
        return new String[] {"Take 2_Coin_tokens. You_may trash a_card from your_hand and_then pay any_number of Coin_tokens. If_you_did trash a_card, gain a_card with a_cost of up_to the_cost of the_trashed_card plus the number of Coin_tokens you_paid."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoinTokens(player, game, 2);
        // you may trash a card from your hand
        if (!player.getHand().isEmpty()) {
            Card toTrash = promptChooseTrashFromHand(
                player,
                game,
                new HashSet<>(player.getHand()),
                this.toString() + ": You may trash a card from your hand, then pay any number of Coin tokens and gain a card with a cost of up to the combined value.",
                "Trash nothing"
            );
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                // then spend any number of coin tokens
                int toSpend = chooseSpendCoinTokens(player, game, toTrash);
                if (toSpend != 0) {
                    game.messageAll("spending " + toSpend + " coin token" + (toSpend == 1 ? "" : "s"));
                    player.addCoinTokens(-toSpend);
                }
                // gain a card with a cost of up to the combined value
                Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + toSpend);
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
                }
            }
        }
    }

    private int chooseSpendCoinTokens(Player player, Game game, Card trashed) {
        int max = player.getCoinTokens();
        if (player instanceof Bot) {
            int toSpend = ((Bot) player).butcherSpendCoinTokens(max, trashed);
            if (toSpend < 0 || toSpend > max) {
                throw new IllegalStateException();
            }
            return toSpend;
        }
        String[] choices = new String[max + 1];
        for (int i = 0; i <= max; i++) {
            choices[i] = i + "";
        }
        return new Prompt(player, game)
                .message(this.toString() + ": Spend how many coin tokens?")
                .multipleChoices(choices)
                .responseMultipleChoiceIndex();
    }

}
