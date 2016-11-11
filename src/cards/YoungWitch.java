package cards;

import server.*;

import java.util.List;

public class YoungWitch extends Card {

    public YoungWitch() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 2);
        // discard 2 cards
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = game.promptDiscardNumber(player, 2, "Young Witch");
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
                game.message(target, "You gain " + Cards.CURSE.htmlName());
                game.messageOpponents(target, target.username + " gains " + Cards.CURSE.htmlName());
                game.gain(target, Cards.CURSE);
            }
        }
    }

    private boolean chooseRevealBane(Player target, Game game) {
        if (target instanceof Bot) {
            return ((Bot) target).youngWitchRevealBane();
        }
        int choice = game.promptMultipleChoice(target, "Young Witch: Reveal a bane card, or gain " + Cards.CURSE.htmlName() + "?", "attackPrompt", new String[] {"Reveal bane", "Gain Curse"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "Discard 2 cards.", "Each other player may reveal a Bane card from their hand. If they don't, they gain a Curse.", "Setup: Add an extra Kingdom card pile costing $2 or $3 to the Supply. Cards from that pile are Bane cards."};
    }

    @Override
    public String toString() {
        return "Young Witch";
    }

    @Override
    public String plural() {
        return "Young Witches";
    }

}
