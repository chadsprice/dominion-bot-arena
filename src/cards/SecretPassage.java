package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;

public class SecretPassage extends Card {

    public SecretPassage() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        plusActions(player, game, 1);
        if (!player.getHand().isEmpty()) {
            // choose a card from your hand
            Card card = game.promptChoosePutOnDeck(player, new HashSet<Card>(player.getHand()), "Secret Passage: Choose a card from your hand to put anywhere in your deck.");
            // choose where to put it in your deck
            int numPositions = player.getDraw().size() + 1;
            String[] choices = new String[numPositions];
            for (int i = 0; i < numPositions; i++) {
                String positionStr = i + "";
                if (i == 0) {
                    positionStr = "On top";
                } else if (i == numPositions - 1) {
                    positionStr = "At bottom";
                }
                choices[i] = positionStr;
            }
            int choice = game.promptMultipleChoice(player, "Secret Passage: Choose to put the " + card.htmlNameRaw() + " on top of your deck, at the bottom, or some number of cards from the top.", choices);
            String choiceStr = Card.numCards(choice) + " from the top";
            if (choice == 0) {
                choiceStr = "on top";
            } else if (choice == numPositions - 1) {
                choiceStr = "at the bottom";
            }
            // put it at that position
            game.message(player, "putting " + card.htmlName() + " " + choiceStr + " of your deck");
            game.messageOpponents(player, "putting a card " + choiceStr + " of their deck");
            player.removeFromHand(card);
            player.putInDraw(card, choice);
        } else {
            game.messageAll("having no card in hand");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "+1 Action", "Take a card from your hand and put it anywhere in your deck."};
    }

    @Override
    public String toString() {
        return "Secret Passage";
    }

}
