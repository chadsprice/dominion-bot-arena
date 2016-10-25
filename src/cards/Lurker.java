package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.*;

public class Lurker extends Card {

    public Lurker() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        int choice = game.promptMultipleChoice(player, "Lurker: Trash an action card from the supply, or gain an action card from the trash?", new String[] {"Trash", "Gain"});
        if (choice == 0) {
            Set<Card> trashable = actions(game.supply.keySet());
            if (!trashable.isEmpty()) {
                Card toTrash = game.promptChooseGainFromSupply(player, trashable, "Lurker: Choose an action card from the supply to trash.");
                game.messageAll("trashing " + toTrash.htmlName() + " from the supply");
                game.takeFromSupply(toTrash);
                game.addToTrash(player, toTrash, false);
            } else {
                game.messageAll("there are no action cards in the supply to trash");
            }
        } else {
            List<Card> gainableSorted = new ArrayList<Card>(actions(game.getTrash()));
            Collections.sort(gainableSorted, Player.HAND_ORDER_COMPARATOR);
            if (!gainableSorted.isEmpty()) {
                String[] choices = new String[gainableSorted.size()];
                for (int i = 0; i < gainableSorted.size(); i++) {
                    choices[i] = gainableSorted.get(i).toString();
                }
                choice = game.promptMultipleChoice(player, "Lurker: Choose an action card to gain from the trash.", choices);
                Card toGain = gainableSorted.get(choice);
                game.messageAll("gaining " + toGain.htmlName() + " from the trash");
                game.gainFromTrash(player, toGain);
            } else {
                game.messageAll("but there are no action cards in the trash to gain");
            }
        }
    }

    private Set<Card> actions(Collection<Card> cards) {
        Set<Card> actions = new HashSet<Card>();
        for (Card card : cards) {
            if (card.isAction) {
                actions.add(card);
            }
        }
        return actions;
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "Choose one: Trash an Action card from the supply; or gain an Action card from the trash."};
    }

    @Override
    public String toString() {
        return "Lurker";
    }

}
