package cards;

import server.*;

import java.util.*;

public class Pillage extends Card {

    @Override
    public String name() {
        return "Pillage";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Trash this. Each other player with 5_or_more cards in_hand reveals their_hand and discards a_card that_you_choose.",
                "Gain 2_[Spoils] from the [Spoils]_pile."
        };
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
            Card toDiscard = ((Bot) player).pillageOpponentDiscard(Collections.unmodifiableSet(discardable));
            checkContains(discardable, toDiscard);
            return toDiscard;
        }
        return promptMultipleChoiceCard(
                player,
                game,
                Prompt.Type.NORMAL,
                this.toString() + ": " + target.username + " has " + Card.htmlList(target.getHand()) + ". Which do they discard?",
                discardable
        );
    }

}
