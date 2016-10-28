package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Ironmonger extends Card {

    public Ironmonger() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // reveal the top card of your deck
        List<Card> top = player.takeFromDraw(1);
        if (!top.isEmpty()) {
            Card revealed = top.get(0);
            game.messageAll("drawing " + revealed.htmlName());
            // you may discard it
            if (chooseDiscard(player, game, revealed)) {
                game.messageAll("discarding it");
                player.addToDiscard(revealed);
            } else {
                game.messageAll("putting it back on top");
                player.putOnDraw(revealed);
            }
            if (revealed.isAction) {
                plusActions(player, game, 1);
            }
            if (revealed.isTreasure) {
                plusCoins(player, game, 1);
            }
            if (revealed.isVictory) {
                plusCards(player, game, 1);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean chooseDiscard(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).ironmongerDiscardTopOfDeck(card);
        }
        int choice = game.promptMultipleChoice(player, "Ironmonger: You draw " + card.htmlName() + ". Discard it or put it back?", new String[] {"Discard", "Put back"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Reveal the top card of your deck;", "you may discard it.", "Either way, it if is an...", "Action card, +1 Action", "Treasure card, +$1", "Victory card, +1 Card"};
    }

    @Override
    public String toString() {
        return "Ironmonger";
    }

}
