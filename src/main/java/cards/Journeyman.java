package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Journeyman extends Card {

    @Override
    public String name() {
        return "Journeyman";
    }

    @Override
    public String plural() {
        return "Journeymen";
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
        return new String[] {"Name a_card. Reveal_cards from the_top of your_deck until you_reveal 3_cards that are not the_named_card. Put those_cards into your_hand and discard_the_rest."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        // name a card
        Card named = chooseNameACard(player, game);
        // reveal cards until 3 that are not the named card, putting those into your hand and discarding the rest
        revealUntil(
                player,
                game,
                c -> c != named,
                3,
                list -> putRevealedIntoHand(player, game, list)
        );
    }

    private Card chooseNameACard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).journeymanNameACard();
        }
        return promptNameACard(
                player,
                game,
                this.toString(),
                "Name a card."
        );
    }

}
