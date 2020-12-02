package cards;

import server.*;

import java.util.HashSet;
import java.util.Set;

public class Trader extends Card {

    @Override
    public String name() {
        return "Trader";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public String htmlType() {
        return "Action-Reaction";
    }

    @Override
    public String htmlHighlightType() {
        return "reaction";
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Trash a card from your_hand.",
                "Gain a number of_[Silvers] equal_to its_cost in_coins.",
                "When you would gain a_card, you_may reveal_this from your_hand.",
                "If you do, instead, gain a_[Silver]."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        if (!player.getHand().isEmpty()) {
            Card toTrash = chooseTrash(player, game);
            int numSilvers = Math.min(toTrash.cost(game), game.supply.get(Cards.SILVER));
            game.messageAll("trashing " + toTrash + " and gaining " + Cards.SILVER.htmlName(numSilvers));
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            for (int i = 0; i < numSilvers; i++) {
                game.gain(player, Cards.SILVER);
            }
        } else {
            game.messageAll("having no card in hand to trash");
        }
    }

    private Card chooseTrash(Player player, Game game) {
        Set<Card> trashable = new HashSet<>(player.getHand());
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).traderTrash(trashable);
            checkContains(trashable, toTrash);
            return toTrash;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": Choose a card to trash, and gain a number of Silvers equal to its cost in coins.")
                .handChoices(trashable)
                .responseCard();
    }

}
