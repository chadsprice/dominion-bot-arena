package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class Butcher extends Card {

    public Butcher() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoinTokens(player, game, 2);
        // you may trash a card from your hand
        if (!player.getHand().isEmpty()) {
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()),
                    this.toString() + ": You may trash a card from your hand, then pay any number of Coin tokens and gain a card with a cost of up to the combined value."
                    , false, "Trash nothing");
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
                    Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain.");
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
        return game.promptMultipleChoice(player, this.toString() + ": Spend how many coin tokens?", choices);
    }

    @Override
    public String[] description() {
        return new String[] {"Take 2 coin tokens. You may trash a card from your hand and then pay any number of Coin tokens. If you did trash a card, gain a card with a cost of up to the cost of the trashed card plus the number of Coin tokens you paid."};
    }

    @Override
    public String toString() {
        return "Butcher";
    }

}
