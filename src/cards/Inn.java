package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Inn extends Card {

    public Inn() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        plusActions(player, game, 2);
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = game.promptDiscardNumber(player, 2, "Inn");
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
    }

    @Override
    public void onGain(Player player, Game game) {
        // shuffle any number of action cards from your discard back into your deck
        List<Card> innable = player.getDiscard().stream().filter(c -> c.isAction).collect(Collectors.toList());
        if (!innable.isEmpty()) {
            List<Card> toInn = chooseInn(player, game, new ArrayList<>(innable));
            if (!toInn.isEmpty()) {
                game.message(player, "shuffling " + Card.htmlList(toInn) + " into your deck");
                game.messageOpponents(player, "shuffling " + Card.htmlList(toInn) + " into their deck");
                player.removeFromDiscard(toInn);
                player.shuffleIntoDraw(toInn);
            }
        }
    }

    private List<Card> chooseInn(Player player, Game game, List<Card> innable) {
        if (player instanceof Bot) {
            List<Card> toInn = ((Bot) player).innShuffleIntoDeck(new ArrayList<>(innable));
            // check the bot's response
            for (Card eachToInn : toInn) {
                if (!innable.remove(eachToInn)) {
                    throw new IllegalStateException();
                }
            }
            return toInn;
        }
        Collections.sort(innable, Player.HAND_ORDER_COMPARATOR);
        List<Card> toInn = new ArrayList<>();
        while (!innable.isEmpty()) {
            Card nextToInn = sendPromptInn(player, game, innable);
            if (nextToInn != null) {
                innable.remove(nextToInn);
                toInn.add(nextToInn);
            } else {
                break;
            }
        }
        return toInn;
    }

    private Card sendPromptInn(Player player, Game game, List<Card> innable) {
        String[] choices = new String[innable.size() + 1];
        for (int i = 0; i < innable.size(); i++) {
            choices[i] = innable.get(i).toString();
        }
        choices[choices.length - 1] = "Done";
        int choice = game.promptMultipleChoice(player, "Inn: Choose any number of action cards in your discard to shuffle into your deck", choices);
        if (choice == choices.length - 1) {
            return null;
        } else {
            return innable.get(choice);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "+2 Actions", "Discard 2 cards.", "When you gain this, look through your discard pile (including this), reveal any number of Action cards from it, and shuffle them into your deck."};
    }

    @Override
    public String toString() {
        return "Inn";
    }

}
