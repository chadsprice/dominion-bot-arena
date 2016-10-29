package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.*;

public class Scavenger extends Card {

    public Scavenger() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
        // you may put your deck into your discard pile
        if (!player.getDraw().isEmpty() && choosePutDeckIntoDiscard(player, game)) {
            game.message(player, "putting your deck into your discard pile");
            game.messageOpponents(player, "putting their deck into their discard pile");
            // this does not trigger the Tunnel reaction
            player.addToDiscard(player.takeFromDraw(player.getDraw().size()), false);
        }
        if (!player.getDiscard().isEmpty()) {
            Card toPutOnDeck = choosePutFromDiscardOntoDeck(player, game, new HashSet<>(player.getDiscard()));
            game.message(player, "putting " + toPutOnDeck.htmlName() + " from your discard on top of your deck");
            game.messageOpponents(player, "putting " + toPutOnDeck.htmlName() + " from their discard on top of their deck");
            player.removeFromDiscard(toPutOnDeck, 1);
            player.putOnDraw(toPutOnDeck);
        } else {
            game.message(player, "your discard is empty");
            game.messageOpponents(player, "their discard is empty");
        }
    }

    private boolean choosePutDeckIntoDiscard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).scavengerPutDeckIntoDiscard();
        }
        int choice = game.promptMultipleChoice(player, "Scavenger: Put your deck into your discard pile?", new String[] {"Yes", "No"});
        return (choice == 0);
    }

    private Card choosePutFromDiscardOntoDeck(Player player, Game game, Set<Card> cards) {
        if (player instanceof Bot) {
            Card card = ((Bot) player).scavengerPutFromDiscardOntoDeck(cards);
            // check the bot's response
            if (!cards.contains(card)) {
                throw new IllegalStateException();
            }
            return card;
        }
        List<Card> cardsSorted = new ArrayList<>(cards);
        Collections.sort(cardsSorted, Player.HAND_ORDER_COMPARATOR);
        String[] choices = new String[cardsSorted.size()];
        for (int i = 0; i < cardsSorted.size(); i++) {
            choices[i] = cardsSorted.get(i).toString();
        }
        int choice = game.promptMultipleChoice(player, "Scavenger: Choose a card in your discard to put on top of your deck.", choices);
        return cardsSorted.get(choice);
    }

    @Override
    public String[] description() {
        return new String[] {"+$2", "You may put your deck into your discard pile. Look through your discard pile and put one card from it on top of your deck."};
    }

    @Override
    public String toString() {
        return "Scavenger";
    }

}
