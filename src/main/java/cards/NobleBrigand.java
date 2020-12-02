package cards;

import server.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NobleBrigand extends Card {

    @Override
    public String name() {
        return "Noble Brigand";
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
                "<+1$>",
                "When you buy this or play_it, each other_player reveals the_top 2_cards of their_deck, trashes a revealed [Silver] or [Gold] you_choose, and discards the_rest. If_they didn't reveal a_Treasure, they gain a_[Copper].",
                "You gain the trashed cards."
        };
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
            boolean revealedTreasure = top.stream().anyMatch(Card::isTreasure);
            // trash a revealed Silver or Gold, attacker's choice
            Set<Card> silverOrGold = top.stream().filter(c -> c == Cards.SILVER || c == Cards.GOLD).collect(Collectors.toSet());
            Card toTrash = null;
            if (silverOrGold.size() == 2) {
                toTrash = chooseTrashGoldOverSilver(player, game, target) ? Cards.GOLD : Cards.SILVER;
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
            if (!revealedTreasure && game.supply.get(Cards.COPPER) != 0) {
                game.messageAll("gaining " + Cards.COPPER.htmlName());
                game.gain(target, Cards.COPPER);
            }
            game.messageIndent--;
        }
    }

    private boolean chooseTrashGoldOverSilver(Player player, Game game, Player target) {
        if (player instanceof Bot) {
            return ((Bot) player).nobleBrigandTrashGoldOverSilver();
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": " + target.username + " reveals " + Cards.GOLD.htmlName() + " and " + Cards.SILVER + ". Which one do they trash? (You gain the trashed card.)")
                .multipleChoices(new String[] {"Gold", "Silver"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}
