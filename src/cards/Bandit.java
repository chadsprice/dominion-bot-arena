package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.*;

public class Bandit extends Card {

    public Bandit() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // gain a gold
        if (game.supply.get(Card.GOLD) != 0) {
            game.messageAll("gaining " + Card.GOLD.htmlName());
            game.gain(player, Card.GOLD);
        }
        // each other player reveals the top 2 cards of their deck, then trashes a non-copper treasure
        for (Player target : targets) {
            List<Card> revealed = target.takeFromDraw(2);
            if (!revealed.isEmpty()) {
                game.message(target, "you reveal " + Card.htmlList(revealed));
                game.messageOpponents(target, target.username + " reveals " + Card.htmlList(revealed));
                Set<Card> trashable = nonCopperTreasures(revealed);
                if (!trashable.isEmpty()) {
                    Card toTrash = null;
                    if (trashable.size() == 2) {
                        // target chooses which one to trash
                        String[] choices = new String[2];
                        List<Card> trashableSorted = new ArrayList<Card>(trashable);
                        Collections.sort(trashableSorted, Player.HAND_ORDER_COMPARATOR);
                        for (int i = 0; i < 2; i++) {
                            choices[i] = trashableSorted.get(i).toString();
                        }
                        int choice = game.promptMultipleChoice(target, "Bandit: You reveal " + Card.htmlList(trashableSorted) + ". Choose one to trash.", "attackPrompt", choices);
                        toTrash = trashableSorted.get(choice);
                    } else { // trashable.size() == 1
                        toTrash = trashable.iterator().next();
                    }
                    game.message(target, "you trash " + toTrash.htmlName());
                    game.messageOpponents(target, target.username + " trashes " + toTrash.htmlName());
                    revealed.remove(toTrash);
                    game.trash.add(toTrash);
                }
            } else {
                game.message(target, "your deck is empty");
                game.messageOpponents(target, target.username + "'s deck is empty");
            }
        }
    }

    private Set<Card> nonCopperTreasures(List<Card> revealed) {
        Set<Card> nonCopperTreasures = new HashSet<Card>();
        for (Card card : revealed) {
            if (card.isTreasure && card != card.COPPER) {
                nonCopperTreasures.add(card);
            }
        }
        return nonCopperTreasures;
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a Gold. Each other player reveals the top 2 cards of their deck, trashes a revealed Treasure other than Copper, and discards the rest."};
    }

    @Override
    public String toString() {
        return "Bandit";
    }

}
