package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Count extends Card {

    public Count() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        switch (chooseBenefit(player, game, true)) {
            case 0:
                // discard 2 cards
                if (!player.getHand().isEmpty()) {
                    List<Card> toDiscard = game.promptDiscardNumber(player, 2, "Count", "actionPrompt");
                    game.messageAll("discarding " + Card.htmlList(toDiscard));
                    player.putFromHandIntoDiscard(toDiscard);
                } else {
                    game.messageAll("revealing an empty hand, discarding nothing");
                }
                break;
            case 1:
                // put a card from your hand on top of your deck
                if (!player.getHand().isEmpty()) {
                    Card toPutOnDeck = game.promptChoosePutOnDeck(player, new HashSet<>(player.getHand()), "Count: Choose a card to put on top of your deck.");
                    game.message(player, "putting " + toPutOnDeck + " on top of your deck");
                    game.message(player, "putting a card on top of their deck");
                    player.putFromHandOntoDraw(toPutOnDeck);
                } else {
                    game.message(player, "revealing an empty hand, putting nothing on top of your deck");
                    game.messageOpponents(player, "revealing an empty hand, putting nothing on top of their deck");
                }
                break;
            default: // 2
                // gain a Copper
                if (game.supply.get(Card.COPPER) != 0) {
                    game.messageAll("gaining " + Card.COPPER.htmlName());
                    game.gain(player, Card.COPPER);
                } else {
                    game.messageAll("gaining nothing");
                }
        }
        switch (chooseBenefit(player, game, false)) {
            case 0:
                // +$3
                plusCoins(player, game, 3);
                break;
            case 1:
                if (!player.getHand().isEmpty()) {
                    List<Card> toTrash = new ArrayList<>(player.getHand());
                    game.messageAll("trashing " + Card.htmlList(toTrash));
                    player.removeFromHand(toTrash);
                    game.trash(player, toTrash);
                } else {
                    game.messageAll("revealing an empty hand, trashing nothing");
                }
                break;
            default: // 2
                // gain a Duchy
                if (game.supply.get(Card.DUCHY) != 0) {
                    game.messageAll("gaining " + Card.DUCHY.htmlName());
                    game.gain(player, Card.DUCHY);
                } else {
                    game.messageAll("gaining nothing");
                }
        }
    }

    private int chooseBenefit(Player player, Game game, boolean isFirst) {
        if (player instanceof Bot) {
            int choice;
            if(isFirst) {
                choice = ((Bot) player).countFirstBenefit();
            } else {
                choice = ((Bot) player).countSecondBenefit();
            }
            if (choice < 0 || choice > 2) {
                throw new IllegalStateException();
            }
            return choice;
        }
        if (isFirst) {
            return game.promptMultipleChoice(player, "Count: Choose one", new String[] {"Discard 2 cards", "Put card on deck", "Gain a Copper"});
        } else {
            return game.promptMultipleChoice(player, "Count: Choose one", new String[] {"+$3", "Trash your hand", "Gain a Duchy"});
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Choose one: Discard 2 cards; or put a card from your hand on top of your deck; or gain a Copper.", "Choose one: +$3; or trash your hand; or gain a Duchy."};
    }

    @Override
    public String toString() {
        return "Count";
    }

}
