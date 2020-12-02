package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class HuntingParty extends Card {

    @Override
    public String name() {
        return "Hunting Party";
    }

    @Override
    public String plural() {
        return "Hunting Parties";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "Reveal your_hand. Reveal_cards from your_deck until you reveal a_card that isn't a_duplicate of one in your_hand. Put_it into your_hand and discard_the_rest."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // reveal your hand
        revealHand(player, game);
        // reveal cards from your deck until you reveal a card that isn't a duplicate of one on your hand
        // put it into your hand
        revealUntil(
                player,
                game,
                c -> !player.getHand().contains(c),
                c -> putRevealedIntoHand(player, game, c)
        );
    }

}
