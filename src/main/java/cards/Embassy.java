package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Embassy extends Card {

    @Override
    public String name() {
        return "Embassy";
    }

    @Override
    public String plural() {
        return "Embassies";
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
                "<+5_Cards>",
                "Discard 3_cards.",
                "When you gain_this, each other_player gains a_[Silver]."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 5);
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = promptDiscardNumber(
                    player,
                    game,
                    3
            );
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
    }

    @Override
    public void onGain(Player player, Game game) {
        // each other player gains a Silver
        game.getOpponents(player).forEach(opponent -> {
            if (game.supply.get(Cards.SILVER) != 0) {
                game.message(opponent, "You gain " + Cards.SILVER.htmlName() + " because of " + this.htmlNameRaw());
                game.messageOpponents(opponent, opponent.username + " gains " + Cards.SILVER.htmlName() + " because of " + this.htmlNameRaw());
                game.gain(opponent, Cards.SILVER);
            }
        });
    }

}
