package cards;

import server.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SecretPassage extends Card {

    @Override
    public String name() {
        return "Secret Passage";
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
        return new String[] {
                "<+2_Cards>",
                "<+1_Action>",
                "Take a_card from your_hand and put_it anywhere in your_deck."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        plusActions(player, game, 1);
        if (!player.getHand().isEmpty()) {
            // choose a card from your hand
            Card card = chooseCardFromHand(player, game);
            // choose where to put it in your deck
            int locationIndex;
            if (!player.getDraw().isEmpty()) {
                locationIndex = chooseLocationInDeck(player, game, card);
            } else {
                locationIndex = 0;
            }
            String locationStr = Card.numCards(locationIndex) + " from the top";
            if (locationIndex == 0) {
                locationStr = "on top";
            } else if (locationIndex == player.getDraw().size()) {
                locationStr = "at the bottom";
            }
            // put it at that position
            game.message(player, "putting " + card.htmlName() + " " + locationStr + " of your deck");
            game.messageOpponents(player, "putting a card " + locationStr + " of their deck");
            player.removeFromHand(card);
            player.putInDraw(card, locationIndex);
        } else {
            game.messageAll("having an empty hand");
        }
    }

    private Card chooseCardFromHand(Player player, Game game) {
        Set<Card> cards = new HashSet<>(player.getHand());
        if (player instanceof Bot) {
            Card card = ((Bot) player).secretPassageCard(Collections.unmodifiableSet(cards));
            checkContains(cards, card);
            return card;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": Take a card from your hand and put it anywhere in your deck.")
                .handChoices(cards)
                .responseCard();
    }

    private int chooseLocationInDeck(Player player, Game game, Card card) {
        int numLocations = player.getDraw().size() + 1;
        if (player instanceof Bot) {
            int location = ((Bot) player).secretPassageLocation();
            checkMultipleChoice(numLocations, location);
        }
        String[] choices = new String[numLocations];
        for (int i = 0; i < numLocations; i++) {
            String positionStr = i + "";
            if (i == 0) {
                positionStr = "On top";
            } else if (i == numLocations - 1) {
                positionStr = "At bottom";
            }
            choices[i] = positionStr;
        }
        return new Prompt(player, game)
                .message(this.toString() + ": Choose to put the " + card.htmlNameRaw() + " on top of your deck, at the bottom, or some number of cards from the top.")
                .multipleChoices(choices)
                .responseMultipleChoiceIndex();
    }

}
