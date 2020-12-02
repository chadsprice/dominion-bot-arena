package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Rogue extends Card {

    @Override
    public String name() {
        return "Rogue";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2$>",
                "If there are any_cards in the_trash costing from_3$ to_6$, gain one of_them. Otherwise, each other_player reveals the_top 2_cards of their_deck, trashes one of_them costing from_3$ to_6$, and discards the_rest."
        };
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        Set<Card> gainable = game.getTrash().stream()
                .filter(c -> 3 <= c.cost(game) && c.cost(game) <= 6)
                .collect(Collectors.toSet());
        if (!gainable.isEmpty()) {
            chooseGainFromTrash(
                    player,
                    game,
                    gainable,
                    this.toString() + ": Choose a card to gain from the trash costing from $3 to $6."
            );
        } else {
            // each other player reveals the top 2 cards of their deck, trashes one of them costing from $3 to $6
            topTwoCardsAttack(
                    targets,
                    game,
                    c -> 3 <= c.cost(game) && c.cost(game) <= 6
            );
        }
    }

}
