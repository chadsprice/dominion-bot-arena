package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NobleBrigand extends Card {

    public NobleBrigand() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCoins(player, game, 1);
        onBuyOrPlay(player, game, targets);
    }

    public void onBuyOrPlay(Player player, Game game, List<Player> targets) {
        for (Player target : targets) {
            // reveal the top 2 cards of their deck
            List<Card> top = target.takeFromDraw(2);
            game.message(target, "You reveal " + Card.htmlList(top));
            game.messageOpponents(target, target.username + " reveals " + Card.htmlList(top));
            game.messageIndent++;
            boolean revealedTreasure = top.stream().anyMatch(c -> c.isTreasure);
            // trash a revealed Silver or Gold, attacker's choice
            Set<Card> silverOrGold = top.stream().filter(c -> c == Card.SILVER || c == Card.GOLD).collect(Collectors.toSet());
            Card toTrash = null;
            if (silverOrGold.size() == 2) {
                toTrash = chooseTrashGoldOverSilver(player, game, target) ? Card.GOLD : Card.SILVER;
            } else if (silverOrGold.size() == 1) {
                toTrash = silverOrGold.iterator().next();
            }
            if (toTrash != null) {
                game.message(target, "You trash the " + toTrash.htmlNameRaw() + " and " + player.username + " gains it");
                for (Player targetOpponent : game.getOpponents(target)) {
                    String gainStr = (targetOpponent == player) ? "you gain it" : player.username + " gains it";
                    game.message(targetOpponent, target.username + " trashes the " + toTrash.htmlNameRaw() + " and " + gainStr);
                }
                top.remove(toTrash);
                game.trash(target, toTrash);
                // attacker gains the trashed cards
                game.gainFromTrash(player, toTrash);
            }
            // discard the rest
            if (!top.isEmpty()) {
                game.messageAll("discarding the rest");
                target.addToDiscard(top);
            }
            // if they didn't reveal a treasure, they gain a Copper
            if (!revealedTreasure && game.supply.get(Card.COPPER) != 0) {
                game.messageAll("gaining " + Card.COPPER.htmlName());
                game.gain(target, Card.COPPER);
            }
            game.messageIndent--;
        }
    }

    private boolean chooseTrashGoldOverSilver(Player player, Game game, Player target) {
        if (player instanceof Bot) {
            return ((Bot) player).nobleBrigandTrashGoldOverSilver();
        }
        int choice = game.promptMultipleChoice(player, "Noble Brigand: " + target.username + " reveals " + Card.GOLD.htmlName() + " and " + Card.SILVER + ". Trash and gain which one?", new String[] {"Gold", "Silver"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"+$1", "When you buy this or play it, each other player reveals the top 2 cards of their deck, trashes a revealed Silver or Gold you choose, and discards the rest. If they didn't reveal a Treasure, they gain a Copper.", "You gain the trashed cards."};
    }

    @Override
    public String toString() {
        return "Noble Brigand";
    }

}
