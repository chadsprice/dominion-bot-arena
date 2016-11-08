package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Advisor extends Card {

    public Advisor() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
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
        if (chooser instanceof Bot) {
            Card toDiscard = ((Bot) chooser).advisorChooseOpponentDiscard(discardable);
            if (!discardable.contains(toDiscard)) {
                throw new IllegalStateException();
            }
            return toDiscard;
        }
        return game.promptMultipleChoiceCard(chooser,
                this.toString() + ": " + player.username + " draws " + Card.htmlList(drawn) + ". Choose which one they discard",
                "actionPrompt", discardable);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "Reveal the top 3 cards of your deck.", "The player to your left chooses one of them. Discard that card. Put the other cards into your hand."};
    }

    @Override
    public String toString() {
        return "Advisor";
    }

}
