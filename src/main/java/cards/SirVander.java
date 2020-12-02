package cards;

import server.Cards;
import server.Game;
import server.Player;

import java.util.List;

public class SirVander extends Knight {

    @Override
    public String name() {
        return "Sir Vander";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add("When you trash_this, gain a_[Gold].");
    }

    @Override
    public void onTrash(Player player, Game game) {
        if (game.supply.get(Cards.GOLD) != 0) {
            game.messageAll("gaining " + Cards.GOLD.htmlName() + " because of " + this.htmlNameRaw());
            game.gain(player, Cards.GOLD);
        }
    }

}
