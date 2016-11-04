package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class HorseTraders extends Card {

    public HorseTraders() {
        isAction = true;
        isAttackReaction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        plusCoins(player, game, 3);
        // discard 2 cards
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = game.promptDiscardNumber(player, 2, "Horse Traders");
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
    }

    @Override
    public boolean onAttackReaction(Player player, Game game) {
        game.messageAll("setting it aside");
        player.removeFromHand(this);
        // at the start of your next turn, treat this as a duration effect
        player.addDurationEffect(this);
        return false;
    }

    @Override
    public void onDurationEffect(Player player, Game game) {
        plusCards(player, game, 1);
        // return this card to your hand
        game.message(player, "returning the " + this.htmlNameRaw() + " to your hand");
        game.messageOpponents(player, "returning the " + this.htmlNameRaw() + " to their hand");
        player.removeDurationSetAside(this);
        player.addToHand(this);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Buy", "+$3", "Discard 2 cards.", "When another player plays an Attack card, you may set this aside from your hand. If you do, then at the start of your next turn , +1 Card and return this to your hand."};
    }

    @Override
    public String toString() {
        return "Horse Traders";
    }

    @Override
    public String plural() {
        return toString();
    }

}
