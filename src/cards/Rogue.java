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
            // each other player reveals the top 2 cards of their deck, trashes one of them costing from $3 to $6
            topTwoCardsAttack(targets,  game,
                    c -> 3 <= c.cost(game) && c.cost(game) <= 6,
                    c -> {});
        }
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
