package cards;

import server.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Advisor extends Card {

    @Override
    public String name() {
        return "Advisor";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[]{
                "<+1_Action>",
                "Reveal the top_3_cards of your_deck.",
                "The player to_your_left chooses one_of_them. Discard_that_card. Put the_other_cards into_your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        // reveal the top 3 cards of your deck
        List<Card> drawn = player.takeFromDraw(3);
        if (!drawn.isEmpty()) {
            game.messageAll("drawing " + Card.htmlList(drawn));
            game.messageIndent++;
            // the player to your left chooses one of them for you to discard
            Card toDiscard = chooseDiscard(player, game, drawn);
            game.messageAll("discarding the " + toDiscard.htmlNameRaw());
            drawn.remove(toDiscard);
            player.addToDiscard(toDiscard);
            // put the other cards into your hand
            if (!drawn.isEmpty()) {
                game.message(player, "putting the rest into your hand");
                game.messageOpponents(player, "putting the rest into their hand");
                player.addToHand(drawn);
            }
            game.messageIndent--;
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private Card chooseDiscard(Player player, Game game, List<Card> drawn) {
        Player chooser = game.getOpponents(player).get(0);
        Set<Card> discardable = new HashSet<>(drawn);
        return game.prompt(
                chooser,
                bot -> {
                    Card toDiscard = bot.advisorOpponentDiscard(Collections.unmodifiableSet(discardable));
                    checkContains(discardable, toDiscard);
                    return toDiscard;
                },
                () -> promptMultipleChoiceCard(
                        chooser,
                        game,
                        Prompt.Type.NORMAL,
                        this.toString() + ": " + player.username + " draws " + Card.htmlList(drawn) + ". Choose which one they discard",
                        discardable
                )
        );
    }
}
