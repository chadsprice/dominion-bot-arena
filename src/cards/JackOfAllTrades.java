package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JackOfAllTrades extends Card {

    public JackOfAllTrades() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        // gain a Silver
        if (game.supply.get(Card.SILVER) != 0) {
            game.messageAll("gaining " + Card.SILVER.htmlName());
            game.gain(player, Card.SILVER);
        }
        // look at the top card of your deck and discard it or put it back
        List<Card> drawn = player.takeFromDraw(1);
        if (!drawn.isEmpty()) {
            Card card = drawn.get(0);
            if (chooseDiscardTopOfDeck(player, game, card)) {
                game.message(player, "discarding " + card.htmlName() + " from the top of your deck");
                game.messageOpponents(player, "discarding " + card.htmlName() + " from the top of their deck");
                player.addToDiscard(card);
            } else {
                game.message(player, "leaving the " + card.htmlNameRaw() + " on top of your deck");
                game.messageOpponents(player, "leaving the card on top of their deck");
                player.putOnDraw(card);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
        // draw until you have 5 cards in hand
        if (player.getHand().size() < 5) {
            plusCards(player, game, 5 - player.getHand().size());
        }
        // you may trash a card from your hand that is not a treasure
        if (player.getHand().stream().anyMatch(c -> !c.isTreasure)) {
            Set<Card> trashable = player.getHand().stream().filter(c -> !c.isTreasure).collect(Collectors.toSet());
            Card toTrash = game.promptChooseTrashFromHand(player, trashable, "Jack of All Trades: You may trash a card that is not a treasure.", false, "Trash nothing");
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.addToTrash(toTrash);
            }
        }
    }

    private boolean chooseDiscardTopOfDeck(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).jackOfAllTradesDiscardTopOfDeck(card);
        }
        int choice = game.promptMultipleChoice(player, "Jack of All Trades: The top card of your deck is " + card.htmlName() + ". Discard it, or leave it on top?", new String[] {"Discard", "Leave on top"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"Gain a Silver.", "Look at the top card of your deck; discard it or put it back.", "Draw until you have 5 cards in hand.", "You may trash a card from your hand that is not a Treasure."};
    }

    @Override
    public String toString() {
        return "Jack of all Trades";
    }

    @Override
    public String plural() {
        return "Jacks of all Trades";
    }

}
