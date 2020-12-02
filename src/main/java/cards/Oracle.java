package cards;

import server.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Oracle extends Card {

    @Override
    public String name() {
        return "Oracle";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Each player (including_you) reveals the_top 2_cards of their_deck. For each_player, choose_one:",
                "* They discard them",
                "* They put them back on_top in_any order they_choose",
                "<+2_Cards>"
        };
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
                    putOnDeckInAnyOrder(target, game, drawn, "Oracle: Put the revealed cards back on your deck in any order");
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
            return ((Bot) player).oracleDiscardSelf(Collections.unmodifiableList(cards));
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": You reveal " + Card.htmlList(cards) + ". Discard them or put them back?")
                .multipleChoices(new String[] {"Discard", "Put back"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private boolean chooseDiscardOpponent(Player player, Player opponent, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            return ((Bot) player).oracleDiscardOppponent(Collections.unmodifiableList(cards));
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": " + opponent.username + " reveals " + Card.htmlList(cards) + ". Will they discard them or put them back?")
                .multipleChoices(new String[] {"Discard", "Put back"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}
