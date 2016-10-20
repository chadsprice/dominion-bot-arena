package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Duchess extends Card {

    public Duchess() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
        // each player looks at the top card of their deck and discards it or puts it back
        List<Player> playerOrder = game.getOpponents(player);
        playerOrder.add(0, player);
        for (Player eachPlayer : playerOrder) {
            List<Card> drawn = eachPlayer.takeFromDraw(1);
            if (!drawn.isEmpty()) {
                Card card = drawn.get(0);
                if (chooseDiscardTopOfDeck(eachPlayer, game, card)) {
                    game.message(eachPlayer, "You discard the " + card.htmlNameRaw() + " from the top of your deck");
                    game.messageOpponents(eachPlayer, eachPlayer.username + " discards " + card.htmlName() + " from the top of their deck");
                    eachPlayer.addToDiscard(card);
                } else {
                    game.message(eachPlayer, "You leave the " + card.htmlNameRaw() + " on top of your deck");
                    game.messageOpponents(eachPlayer, eachPlayer.username + " leaves the card on top of their deck");
                    eachPlayer.putOnDraw(card);
                }
            } else {
                game.message(eachPlayer, "Your deck is empty");
                game.messageOpponents(eachPlayer, eachPlayer.username + "'s deck is empty");
            }
        }
    }

    private boolean chooseDiscardTopOfDeck(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).duchessChooseDiscardTopOfDeck(card);
        }
        int choice = game.promptMultipleChoice(player, "Duchess: The top card of your deck is " + card.htmlName() + ". Discard it, or leave it on top?", new String[] {"Discard", "Leave on top"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"+$2", "Each player (including you) looks at the top card of their deck, and discards it or puts it back.", "In games using this, when you gain a Duchy, you may gain a Duchess."};
    }

    @Override
    public String toString() {
        return "Duchess";
    }

    @Override
    public String plural() {
        return "Duchesses";
    }

}
