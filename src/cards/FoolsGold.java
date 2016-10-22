package cards;

import server.Card;
import server.Game;
import server.Player;

public class FoolsGold extends Card {

    public FoolsGold() {
        isTreasure = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public int treasureValue(Game game) {
        if (game.currentPlayer().playedFoolsGoldThisTurn) {
            return 1;
        } else {
            return 4;
        }
    }

    @Override
    public void onPlay(Player player, Game game) {
        player.playedFoolsGoldThisTurn = true;
    }

    @Override
    public String htmlClass() {
        return "treasure-reaction";
    }

    @Override
    public String htmlType() {
        return "Treasure-Reaction";
    }

    @Override
    public String[] description() {
        return new String[] {"If this is the first time you played a Fool's Gold this turn, this is worth $1, otherwise it's worth $4.", "When another player gains a Province, you may trash this from your hand.", "If you do, gain a Gold, putting it on your deck."};
    }

    @Override
    public String toString() {
        return "Fool's Gold";
    }

}
