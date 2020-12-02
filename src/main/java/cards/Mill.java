package cards;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

import java.util.List;
import java.util.Set;

public class Mill extends Card {

    @Override
    public String name() {
        return "Mill";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.VICTORY);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "You may discard 2_cards, for_<+2$>.",
                "1_VP"
        };
    }

    @Override
    public int victoryValue() {
        return 1;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        if (player.getHand().size() >= 2) {
            // you may discard 2 cards for $2
            List<Card> toDiscard = promptDiscardNumber(
                    player,
                    game,
                    2,
                    Prompt.Amount.EXACT_OR_NONE
            );
            if (toDiscard.size() == 2) {
                game.messageAll("discarding " + Card.htmlList(toDiscard) + " for +$2");
                player.putFromHandIntoDiscard(toDiscard);
                // benefit for discarding 2 cards
                player.coins += 2;
            } else {
                game.messageAll("discarding nothing");
            }
        } else if (player.getHand().size() == 1) {
            // special case: if you have only one card in hand, you are allowed to discard it
            List<Card> toDiscard = promptDiscardNumber(
                    player,
                    game,
                    1,
                    Prompt.Amount.UP_TO
            );
            if (toDiscard.size() == 1) {
                game.messageAll("discarding " + Card.htmlList(toDiscard));
                player.putFromHandIntoDiscard(toDiscard);
            } else {
                game.messageAll("discarding nothing");
            }
        } else {
            game.messageAll("having no cards in hand to discard");
        }
    }

}
