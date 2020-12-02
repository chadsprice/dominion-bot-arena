package cards;

import server.*;

import java.util.Set;

public class MarketSquare extends Card {

    @Override
    public String name() {
        return "Market Square";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public String htmlType() {
        return "Action-Reaction";
    }

    @Override
    public String htmlHighlightType() {
        return "reaction";
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "<+1_Buy>",
                "When one of your cards is_trashed, you_may discard_this from your_hand. If_you_do, gain a_[Gold]."
        };
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
            boolean gainingAGold = (game.supply.get(Cards.GOLD) != 0);
            String str = "discarding " + this.htmlName();
            if (gainingAGold) {
                str += " and gaining " + Cards.GOLD.htmlName();
            }
            game.messageAll(str);
            player.putFromHandIntoDiscard(this);
            game.gain(player, Cards.GOLD);
        }
        return discarding;
    }

    private boolean chooseDiscard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).marketSquareDiscard();
        }
        int choice = new Prompt(player, game)
                .type(Prompt.Type.REACTION)
                .message(this.toString() + ": Discard " + this.htmlNameRaw() + " and gain " + Cards.GOLD.htmlName() + "?")
                .multipleChoices(new String[] {"Discard", "Don't"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}
