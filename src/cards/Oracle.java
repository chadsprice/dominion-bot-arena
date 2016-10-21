package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Oracle extends Card {

    public Oracle() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // for each affected player, including this player
        targets.add(0, player);
        for (Player target : targets) {
            // reveal the top 2 cards of their deck
            List<Card> drawn = target.takeFromDraw(2);
            if (!drawn.isEmpty()) {
                game.message(target, "You reveal " + Card.htmlList(drawn));
                game.messageOpponents(target, target.username + " reveals " + Card.htmlList(drawn));
                boolean isDiscarding;
                if (target == player) {
                    isDiscarding = chooseDiscardSelf(player, game, drawn);
                } else {
                    isDiscarding = chooseDiscardOpponent(player, target, game, drawn);
                }
                game.messageIndent++;
                if (isDiscarding) {
                    game.messageAll("discarding them");
                    target.addToDiscard(drawn);
                } else {
                    game.messageAll("putting them back");
                    putRevealedBackInAnyOrder(player, game, target, drawn);
                }
                game.messageIndent--;
            } else {
                game.message(target, "Your deck is empty");
                game.message(target, target.username + "'s deck is empty");
            }
        }
        plusCards(player, game, 2);
    }

    private boolean chooseDiscardSelf(Player player, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            return ((Bot) player).oracleDiscardSelf(cards);
        }
        int choice = game.promptMultipleChoice(player, "Oracle: You reveal " + Card.htmlList(cards) + ". Discard them or put them back?", new String[] {"Discard", "Put back"});
        return (choice == 0);
    }

    private boolean chooseDiscardOpponent(Player player, Player opponent, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            return ((Bot) player).oracleDiscardOppponent(cards);
        }
        int choice = game.promptMultipleChoice(player, "Oracle: " + opponent.username + " reveals " + Card.htmlList(cards) + ". Will they discard them or put them back?", new String[] {"Discard", "Put back"});
        return (choice == 0);
    }

    private void putRevealedBackInAnyOrder(Player player, Game game, Player target, List<Card> revealed) {
        // don't bother asking the order to return the cards if there are not multiple, or if this is the player that
        // played Oracle (since they will just draw these cards next, regardless of order)
        if (revealed.size() > 1 && target != player) {
            Collections.sort(revealed, Player.HAND_ORDER_COMPARATOR);
            List<Card> toPutOnDeck = new ArrayList<Card>();
            while (!revealed.isEmpty()) {
                String[] choices = new String[revealed.size()];
                for (int i = 0; i < revealed.size(); i++) {
                    choices[i] = revealed.get(i).toString();
                }
                int choice = game.promptMultipleChoice(target, "Oracle: Put the revealed cards back on your deck (the first card you choose will be on top of your deck)", "attackPrompt", choices);
                toPutOnDeck.add(revealed.remove(choice));
            }
            player.putOnDraw(toPutOnDeck);
        } else {
            player.putOnDraw(revealed);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Each player (including you) reveals the top 2 cards of their deck, and you choose one: either they discard them, or they put them back on top in an order they choose.", "+2 Cards"};
    }

    @Override
    public String toString() {
        return "Oracle";
    }

}
