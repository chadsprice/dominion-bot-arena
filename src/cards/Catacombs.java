package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Catacombs extends Card {

    public Catacombs() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        List<Card> drawn = player.takeFromDraw(3);
        if (!drawn.isEmpty()) {
            if (choosePutIntoHand(player, game, drawn)) {
                game.message(player, "looking at and putting " + Card.htmlList(drawn) + " into your hand");
                game.messageOpponents(player, "looking at and putting " + Card.numCards(drawn.size()) + " into their hand");
                player.addToHand(drawn);
            } else {
                game.messageAll("looking at and discarding " + Card.htmlList(drawn));
                player.addToDiscard(drawn);
                plusCards(player, game, 3);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean choosePutIntoHand(Player player, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            return ((Bot) player).catacombsPutIntoHand(cards);
        }
        int choice = game.promptMultipleChoice(player, "Catacombs: Put " + Card.htmlList(cards) + " into your hand, or discard them and then +3 cards?", new String[] {"Put into hand", "Discard, then +3 cards"});
        return (choice == 0);
    }

    @Override
    public void onTrash(Player player, Game game) {
        // gain a cheaper card
        Set<Card> gainable = game.cardsCostingAtMost(this.cost(game) - 1);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Catacombs: Choose a card to gain");
            game.messageAll("gaining " + toGain.htmlName() + " because of " + this.htmlNameRaw());
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Look at the top 3 cards of your deck.", "Choose one: Put them into your hand; or discard them and +3 Cards.", "When you trash this, gain a cheaper card."};
    }

    @Override
    public String toString() {
        return "Catacombs";
    }

    @Override
    public String plural() {
        return toString();
    }

}
