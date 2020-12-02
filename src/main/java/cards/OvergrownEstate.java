package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class OvergrownEstate extends Card {

    @Override
    public String name() {
        return "Overgrown Estate";
    }

    @Override
    public Set<Type> types() {
        return types(Type.VICTORY, Type.SHELTER);
    }

    @Override
    public String htmlType() {
        return "Victory-Shelter";
    }

    @Override
    public String htmlHighlightType() {
        return "victory-shelter";
    }

    @Override
    public int cost() {
        return 1;
    }

    @Override
    public String[] description() {
        return new String[] {
                "0_VP",
                "When you trash_this, <+1_Card>."
        };
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

}
