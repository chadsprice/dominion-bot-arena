package cards;

import server.*;

import java.util.List;
import java.util.Set;

public class YoungWitch extends Card {

    @Override
    public String name() {
        return "Young Witch";
    }

    @Override
    public String plural() {
        return "Young Witches";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Cards>",
                "Discard 2 cards.",
                "Each other player may reveal a_Bane card from their_hand. If_they_don't, they gain a_[Curse].",
                "Setup: Add an extra Kingdom card pile costing 2$ or 3$ to the_Supply. Cards_from that pile are Bane_cards."
        };
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 2);
        // discard 2 cards
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = promptDiscardNumber(player, game, 2);
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
        // each other player may reveal a bane card, if they don't they gain a Curse
        for (Player target : targets) {
            boolean revealingBane = false;
            if (target.getHand().contains(game.baneCard)) {
                revealingBane = chooseRevealBane(target, game);
            }
            if (revealingBane) {
                game.message(target, "You reveal a bane card");
                game.messageOpponents(target, target.username + " reveals a bane card");
            } else {
                if (game.isAvailableInSupply(Cards.CURSE)) {
                    game.message(target, "You gain " + Cards.CURSE.htmlName());
                    game.messageOpponents(target, target.username + " gains " + Cards.CURSE.htmlName());
                    game.gain(target, Cards.CURSE);
                } else {
                    game.message(target, "You gain nothing");
                    game.messageOpponents(target, target.username + " gains nothing");
                }
            }
        }
    }

    private boolean chooseRevealBane(Player target, Game game) {
        if (target instanceof Bot) {
            return ((Bot) target).youngWitchRevealBane();
        }
        int choice = new Prompt(target, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": Reveal a bane card, or gain " + Cards.CURSE.htmlName() + "?")
                .multipleChoices(new String[] {"Reveal bane", "Gain Curse"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}
