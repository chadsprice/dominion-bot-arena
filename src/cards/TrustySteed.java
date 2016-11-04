package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class TrustySteed extends Card {

    public TrustySteed() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onPlay(Player player, Game game) {
        int[] benefits = chooseBenefits(player, game);
        for (Integer benefit : benefits) {
            switch (benefit) {
                case 0:
                    plusCards(player, game, 2);
                    break;
                case 1:
                    plusActions(player, game, 2);
                    break;
                case 2:
                    plusCoins(player, game, 2);
                    break;
                default: // 3
                    int numSilvers = Math.min(game.supply.get(Card.SILVER), 4);
                    if (numSilvers != 0) {
                        game.messageAll("gaining " + Card.SILVER.htmlName(numSilvers));
                        for (int n = 0; n < numSilvers; n++) {
                            game.gain(player, Card.SILVER);
                        }
                    } else {
                        game.messageAll("gaining nothing");
                    }
                    game.message(player, "putting your deck into your discard pile");
                    game.messageOpponents(player, "putting their deck into their discard pile");
                    // this does not trigger the Tunnel reaction
                    player.addToDiscard(player.takeFromDraw(player.getDraw().size()), false);
            }
        }
    }

    private int[] chooseBenefits(Player player, Game game) {
        if (player instanceof Bot) {
            int[] benefits = ((Bot) player).trustySteedBenefits();
            // there must be 2 distinct choices, both in the range of 0-3
            if (benefits.length != 2 ||
                    benefits[0] == benefits[1] ||
                    !(0 <= benefits[0] && benefits[0] < 4) ||
                    !(0 <= benefits[1] && benefits[1] < 4)) {
                throw new IllegalStateException();
            }
            return benefits;
        }
        return chooseTwoDifferentBenefits(player, game, new String[] {"+2 Cards", "+2 Actions", "+$2", "Gain 4 Silvers and put your deck into your discard pile"});
    }

    @Override
    public String[] description() {
        return new String[] {"Choose two: +2 Cards; +2 Actions; +$2; gain 4 Silvers and put your deck into your discard pile.", "(The choices must be different.)", "(This is not in the Supply.)"};
    }

    @Override
    public String toString() {
        return "Trusty Steed";
    }

    @Override
    public String htmlType() {
        return "Action-Prize";
    }

}
