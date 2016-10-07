package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
            // TODO: add a new kind of prompt for this so that bots can understand it
            Card toReveal = game.promptChoosePutOnDeck(player, new HashSet<Card>(player.getHand()), "Courtier: Choose a card to reveal from your hand");
            game.messageAll("revealing " + toReveal.htmlName());
            // count the number of named types
            // (parsing htmlType is the most consistent way to do this because some cards like Watchtower override htmlType to express their types)
            int numTypes = toReveal.htmlType().split("-").length;
            String[] choices = new String[] {"+1 Action", "+1 Buy", "+$3", "Gain a Gold"};
            List<Integer> disabledIndexes = new ArrayList<Integer>();
            int[] benefitIndexes = new int[numTypes];
            for (int i = 0; i < numTypes; i++) {
                int choice = game.promptMultipleChoice(player, "Courtier: Choose one", choices, toIntArray(disabledIndexes));
                benefitIndexes[i] = choice;
                disabledIndexes.add(choice);
            }
            for (Integer benefit : benefitIndexes) {
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
                    default:
                        if (game.supply.get(Card.GOLD) != 0) {
                            game.gain(player, Card.GOLD);
                        } else {
                            game.messageAll("gaining nothing");
                        }
                }
            }
        } else {
            game.messageAll("having no cards in hand");
        }
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
