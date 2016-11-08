package cards;

import server.Card;
import server.Game;
import server.Player;

public class Journeyman extends Card {

    public Journeyman() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // name a card
        Card named = game.promptNameACard(player, this.toString(), "Name a card.");
        // reveal cards until 3 that are not the named card, putting those into your hand and discarding the rest
        revealUntil(player, game,
                c -> c != named, 3,
                list -> putRevealedIntoHand(player, game, list));
    }

    @Override
    public String[] description() {
        return new String[] {"Name a card. Reveal cards from the top of your deck until you reveal 3 cards that are not the named card. Put those cards into your hand and discard the rest."};
    }

    @Override
    public String toString() {
        return "Journeyman";
    }

    @Override
    public String plural() {
        return "Journeymen";
    }

}
