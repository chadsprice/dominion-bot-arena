package cards;

import server.*;

import java.util.*;

public class Pillage extends Card {

    public Pillage() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public boolean onAttack(Player player, Game game, List<Player> targets, boolean hasMoved) {
        // trash this
        boolean movedToTrash = false;
        if (!hasMoved) {
            game.messageAll("trashing the " + this.htmlNameRaw());
            player.removeFromPlay(this);
            game.trash(player, this);
            movedToTrash = true;
        }
        // each other player with 5 or more cards in hand
        targets.forEach(target -> {
            if (target.getHand().size() >= 5) {
                // reveals their hand
                game.message(target, "you reveal your hand: " + Card.htmlList(target.getHand()));
                game.messageOpponents(target, target.username + " reveals their hand: " + Card.htmlList(target.getHand()));
                game.messageIndent++;
                // discards a card that you choose
                Card toDiscard = chooseOpponentDiscard(player, game, target);
                game.messageAll("discarding " + toDiscard.htmlName());
                target.putFromHandIntoDiscard(toDiscard);
                game.messageIndent--;
            }
        });
        // gain 2 Spoils
        int numSpoils = Math.min(2, game.nonSupply.get(Cards.SPOILS));
        game.messageAll("gaining " + Cards.SPOILS.htmlName(numSpoils));
        for (int i = 0; i < 2 && game.nonSupply.get(Cards.SPOILS) != 0; i++) {
            game.gain(player, Cards.SPOILS);
        }
        return movedToTrash;
    }

    private Card chooseOpponentDiscard(Player player, Game game, Player target) {
        Set<Card> discardable = new HashSet<>(target.getHand());
        if (player instanceof Bot) {
            Card toDiscard = ((Bot) player).pillageOpponentDiscard(discardable);
            if (!discardable.contains(toDiscard)) {
                throw new IllegalStateException();
            }
            return toDiscard;
        }
        return game.promptMultipleChoiceCard(player, "Pillage: " + target.username + " has " + Card.htmlList(target.getHand()) + ". Have them discard which?", "actionPrompt", discardable);
    }

    @Override
    public String[] description() {
        return new String[] {"Trash this. Each other player with 5 or more cards in hand reveals their hand and discards a card that you choose.", "Gain 2 Spoils from the Spoils pile."};
    }

    @Override
    public String toString() {
        return "Pillage";
    }

}
