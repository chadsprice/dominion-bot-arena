package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class MarketSquare extends Card {

    public MarketSquare() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        plusBuys(player, game, 1);
    }

    public boolean onCardTrashed(Player player, Game game) {
        boolean discarding = chooseDiscard(player, game);
        if (discarding) {
            boolean gainingAGold = (game.supply.get(Card.GOLD) != 0);
            String str = "discarding " + this.htmlName();
            if (gainingAGold) {
                str += " and gaining " + Card.GOLD.htmlName();
            }
            game.messageAll(str);
            player.putFromHandIntoDiscard(this);
            game.gain(player, Card.GOLD);
        }
        return discarding;
    }

    private boolean chooseDiscard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).marketSquareDiscard();
        }
        int choice = game.promptMultipleChoice(player, "Market Square: Discard " + this.htmlNameRaw() + " and gain " + Card.GOLD.htmlName() + "?", "reactionPrompt", new String[] {"Discard", "Don't"});
        return (choice == 0);
    }

    @Override
    public String htmlClass() {
        return "reaction";
    }

    @Override
    public String htmlType() {
        return "Action-Reaction";
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "+1 Buy", "When one of your cards is trashed, you may discard this from your hand. If you do, gain a Gold."};
    }

    @Override
    public String toString() {
        return "Market Square";
    }

}
