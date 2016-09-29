package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.*;

public class Harbinger extends Card {

    public Harbinger() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        if (!player.getDiscard().isEmpty()) {
            Set<Card> cardsInDiscard = new HashSet<Card>(player.getDiscard());
            List<Card> cardsInDiscardSorted = new ArrayList<Card>(cardsInDiscard);
            Collections.sort(cardsInDiscardSorted, Player.HAND_ORDER_COMPARATOR);
            String[] choices = new String[cardsInDiscardSorted.size() + 1];
            for (int i = 0; i < cardsInDiscardSorted.size(); i++) {
                choices[i] = cardsInDiscardSorted.get(i).toString();
            }
            choices[cardsInDiscardSorted.size()] = "None";
            int choice = game.promptMultipleChoice(player, "Harbinger: Choose a card from your discard to put on top of your deck.", choices);
            if (choice != cardsInDiscardSorted.size()) {
                Card chosenCard = cardsInDiscardSorted.get(choice);
                player.removeFromDiscard(chosenCard, 1);
                player.putOnDraw(chosenCard);
                game.messageAll("putting " + chosenCard.htmlName() + " on top of his deck");
            } else {
                game.messageAll("putting nothing from his discard on top of his deck");
            }
        } else {
            game.message(player, "but your discard is empty");
            game.messageOpponents(player, "but his discard is empty");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Look through your discard pile. You may put a card from it onto your deck."};
    }

    @Override
    public String toString() {
        return "Harbinger";
    }

}
