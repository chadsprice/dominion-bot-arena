package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class OvergrownEstate extends Card {

    public OvergrownEstate() {
        isVictory = true;
    }

    @Override
    public int cost() {
        return 1;
    }

    @Override
    public int victoryValue() {
        return 0;
    }

    @Override
    public void onTrash(Player player, Game game) {
        List<Card> drawn = player.drawIntoHand(1);
        game.message(player, "drawing " + Card.htmlList(drawn) + " because of " + this.htmlNameRaw());
        game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()) + " because of " + this.htmlNameRaw());
    }

    @Override
    public String htmlClass() {
        return "victory-shelter";
    }

    @Override
    public String htmlType() {
        return "Victory-Shelter";
    }

    @Override
    public String[] description() {
        return new String[] {"0 VP", "When you trash this, +1 Card."};
    }

    @Override
    public String toString() {
        return "Overgrown Estate";
    }

}
