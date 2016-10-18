package cards;

import server.Card;
import server.Game;
import server.Player;

public class HuntingParty extends Card {

    public HuntingParty() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // reveal your hand
        revealHand(player, game);
        // reveal cards from your deck until you reveal a card that isn't a duplicate of one on your hand
        // put it into your hand
        revealUntil(player, game,
                c -> !player.getHand().contains(c),
                c -> putRevealedIntoHand(player, game, c));
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Reveal your hand. Reveal cards from your deck until you reveal a card that isn't a duplicate of one in your hand. Put it into your hand and discard the rest."};
    }

    @Override
    public String toString() {
        return "Hunting Party";
    }

    @Override
    public String plural() {
        return "Hunting Parties";
    }

}
