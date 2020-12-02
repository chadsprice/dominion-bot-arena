package cards;

import server.*;

import java.util.HashSet;
import java.util.Set;

public class Hamlet extends Card {

    @Override
    public String name() {
        return "Hamlet";
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
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "You_may discard a_card for <+1_Action>.",
                "You_may discard a_card for <+1_Buy>."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // you may discard a card for +1 action
        if (!player.getHand().isEmpty()) {
            Card toDiscard = chooseDiscardForBenefit(player, game, true);
            if (toDiscard != null) {
                game.messageAll("discarding " + toDiscard.htmlName() + " for +1 action");
                player.putFromHandIntoDiscard(toDiscard);
                player.actions += 1;
            }
        }
        // you may discard a card for +1 buy
        if (!player.getHand().isEmpty()) {
            Card toDiscard = chooseDiscardForBenefit(player, game, false);
            if (toDiscard != null) {
                game.messageAll("discarding " + toDiscard.htmlName() + " for +1 buy");
                player.putFromHandIntoDiscard(toDiscard);
                player.buys += 1;
            }
        }
    }

    private Card chooseDiscardForBenefit(Player player, Game game, boolean benefitIsAction) {
        if (player instanceof Bot) {
            if (benefitIsAction) {
                return ((Bot) player).hamletDiscardForAction();
            } else {
                return ((Bot) player).hamletDiscardForBuy();
            }
        }
        String benefitStr = benefitIsAction ? "+1 action" : "+1 buy";
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may discard a card for " + benefitStr + ".")
                .handChoices(new HashSet<>(player.getHand()))
                .orNone("Discard nothing")
                .responseCard();
    }

}
