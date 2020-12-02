package cards;

import server.*;

import java.util.Set;

public class TrustySteed extends Card {

    @Override
    public String name() {
        return "Trusty Steed";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public String htmlType() {
        return "Action-Prize";
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Choose_two:",
                "* <+2_Cards>",
                "* <+2_Actions>",
                "* <+2$>",
                "* Gain 4_[Silvers] and put your_deck into your discard_pile",
                "(The choices must be different.)",
                "(This_is_not in_the_Supply.)"
        };
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
                    gain(player, game, Cards.SILVER, 4);
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
        return chooseTwoDifferentBenefits(
                player,
                game,
                new String[] {"+2 Cards", "+2 Actions", "+$2", "Gain 4 Silvers and put your deck into your discard pile"}
        );
    }

}
