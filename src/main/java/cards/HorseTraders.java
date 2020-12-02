package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class HorseTraders extends Card {

    @Override
    public String name() {
        return "Horse Traders";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK_REACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Buy>",
                "<+3$>",
                "Discard 2_cards.",
                "When another player plays an_Attack card, you_may set this aside from your_hand. If_you_do, then at the_start of your next_turn, <+1_Card> and return this to_your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        plusCoins(player, game, 3);
        // discard 2 cards
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = promptDiscardNumber(
                    player,
                    game,
                    2
            );
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

}
