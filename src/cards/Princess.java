package cards;

import server.Card;
import server.Game;
import server.Player;

public class Princess extends Card {

    public Princess() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        game.messageAll("cards cost $2 less while this is in play");
        game.costModifierPlayedLastTurn = true;
        game.sendCardCosts();
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Buy", "While this is in play, cards cost $2 less, but not less than $0.", "(This is not in the Supply.)"};
    }

    @Override
    public String toString() {
        return "Princess";
    }

    @Override
    public String htmlType() {
        return "Action-Prize";
    }

}
