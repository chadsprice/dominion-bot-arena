package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;

public class Hamlet extends Card {

    public Hamlet() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
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
                player.addActions(1);
            }
        }
        // you may discard a card for +1 buy
        if (!player.getHand().isEmpty()) {
            Card toDiscard = chooseDiscardForBenefit(player, game, false);
            if (toDiscard != null) {
                game.messageAll("discarding " + toDiscard.htmlName() + " for +1 buy");
                player.putFromHandIntoDiscard(toDiscard);
                player.addBuys(1);
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
        } else {
            String benefitStr = benefitIsAction ? "+1 action" : "+1 buy";
            return game.sendPromptChooseFromHand(player, new HashSet<Card>(player.getHand()), "Hamlet: You may discard a card for " + benefitStr + ".", "actionPrompt", false, "Discard nothing");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "You may discard a card;", "if you do, +1 Action.", "You may discard a card;", "if you do, +1 Buy."};
    }

    @Override
    public String toString() {
        return "Hamlet";
    }

}
