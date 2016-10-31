package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Rogue extends Card {

    public Rogue() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        Set<Card> gainable = game.getTrash().stream()
                .filter(c -> 3 <= c.cost(game) && c.cost(game) <= 6)
                .collect(Collectors.toSet());
        if (!gainable.isEmpty()) {
            chooseGainFromTrash(player, game, gainable, "Rogue: Choose a card to gain from the trash costing from $3 to $6.");
        } else {
            targets.forEach(target -> {
                List<Card> drawn = target.takeFromDraw(2);
                if (!drawn.isEmpty()) {
                    game.message(target, "You draw " + Card.htmlList(drawn));
                    game.messageOpponents(target, target.username + " draws " + Card.htmlList(drawn));
                    game.messageIndent++;
                    Set<Card> trashable = drawn.stream()
                            .filter(c -> 3 <= c.cost(game) && c.cost(game) <= 6)
                            .collect(Collectors.toSet());
                    if (!trashable.isEmpty()) {
                        Card toTrash;
                        if (trashable.size() == 1) {
                            toTrash = trashable.iterator().next();
                        } else {
                            toTrash = chooseTrash(target, game, trashable);
                        }
                        game.messageAll("trashing the " + toTrash.htmlNameRaw());
                        drawn.remove(toTrash);
                        game.addToTrash(target, toTrash);
                    }
                    if (!drawn.isEmpty()) {
                        game.messageAll("discarding the rest");
                        player.addToDiscard(drawn);
                    }
                    game.messageIndent--;
                } else {
                    game.message(target, "Your deck is empty");
                    game.messageOpponents(target, target.username + "'s deck is empty");
                }
            });
        }
    }

    private Card chooseTrash(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).rogueTrash(trashable);
            if (!trashable.contains(toTrash)) {
                throw new IllegalStateException();
            }
            return toTrash;
        }
        return game.promptMultipleChoiceCard(player, "Rogue: You draw " + Card.htmlList(new ArrayList<>(trashable)) + ". Choose one to trash.", "attackPrompt", trashable);
    }

    @Override
    public String[] description() {
        return new String[] {"+$2", "If there are any cards in the trash costing from $3 to $6, gain one of them. Otherwise, each other player reveals the top 2 cards of their deck, trashes one of them costing from $3 to $6, and discards the rest."};
    }

    @Override
    public String toString() {
        return "Rogue";
    }

}
