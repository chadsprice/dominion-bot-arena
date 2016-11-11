package cards;

import server.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Courtier extends Card {

    public Courtier() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        if (!player.getHand().isEmpty()) {
            Card toReveal = chooseReveal(player, game);
            game.messageAll("revealing " + toReveal.htmlName());
            // count the number of named types
            // (parsing htmlType is the most consistent way to do this because some cards like Watchtower override htmlType to express their types)
            int numTypes = toReveal.htmlType().split("-").length;
            for (Integer benefit : chooseBenefits(player, game, Math.min(numTypes, 4))) {
                switch (benefit) {
                    case 0:
                        plusActions(player, game, 1);
                        break;
                    case 1:
                        plusBuys(player, game, 1);
                        break;
                    case 2:
                        plusCoins(player, game, 3);
                        break;
                    default: // case 3
                        gain(player, game, Cards.GOLD);
                }
            }
        } else {
            game.messageAll("having no cards in hand");
        }
    }

    private Card chooseReveal(Player player, Game game) {
        Set<Card> revealable = new HashSet<>(player.getHand());
        if (player instanceof Bot) {
            Card toReveal = ((Bot) player).courtierReveal(revealable);
            if (!revealable.contains(toReveal)) {
                throw new IllegalStateException();
            }
            return toReveal;
        }
        return game.sendPromptChooseFromHand(player, revealable, this.toString() + ": Reveal a card from your hand.", "actionPrompt", true, null);
    }

    private List<Integer> chooseBenefits(Player player, Game game, int numBenefits) {
        if (player instanceof Bot) {
            List<Integer> benefits = ((Bot) player).courtierBenefits(numBenefits);
            // check that there are the right number of benefits, all distinct, and all in the range 0-3
            if (benefits.size() != numBenefits ||
                    benefits.size() != new HashSet<>(benefits).size() ||
                    benefits.stream().anyMatch(b -> b < 0 || b > 3)) {
                throw new IllegalStateException();
            }
            return benefits;
        }
        String numBenefitsStr = (new String[] {"one", "two", "three", "four"})[numBenefits - 1];
        String[] choices = new String[] {"+1 Action", "+1 Buy", "+$3", "Gain a Gold"};
        List<Integer> benefits = new ArrayList<>();
        for (int i = 0; i < numBenefits; i++) {
            int choice = game.promptMultipleChoice(player, this.toString() + ": Choose " + numBenefitsStr + " (the choices must be different)", choices, toIntArray(benefits));
            benefits.add(choice);
        }
        return benefits;
    }

    private int[] toIntArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    @Override
    public String[] description() {
        return new String[] {"Reveal a card from your hand. For each type it has (Action, Attack, etc.), choose one: +1 Action; or +1 Buy; or +$3; or gain a Gold. The choices must be different."};
    }

    @Override
    public String toString() {
        return "Courtier";
    }

}
