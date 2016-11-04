package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Pawn extends Card {

	public Pawn() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
        int[] benefits = chooseBenefits(player, game);
        for (Integer benefit : benefits) {
            switch (benefit) {
                case 0:
                    plusCards(player, game, 1);
                    break;
                case 1:
                    plusActions(player, game, 1);
                    break;
                case 2:
                    plusBuys(player, game, 1);
                    break;
                default:
                    plusCoins(player, game, 1);
            }
        }
	}

	private int[] chooseBenefits(Player player, Game game) {
        if (player instanceof Bot) {
            int[] benefits = ((Bot) player).pawnBenefits();
            // there must be 2 distinct choices, both in the range of 0-3
            if (benefits.length != 2 ||
                    benefits[0] == benefits[1] ||
                    !(0 <= benefits[0] && benefits[0] < 4) ||
                    !(0 <= benefits[1] && benefits[1] < 4)) {
                throw new IllegalStateException();
            }
            return benefits;
        }
        return chooseTwoDifferentBenefits(player, game, new String[] {"+1 Card", "+1 Action", "+1 Buy", "+$1"});
    }

	@Override
	public String[] description() {
		return new String[] {"Choose two: +1 Card; +1 Action; +1 Buy; +$1.", "The choices must be different."};
	}

	@Override
	public String toString() {
		return "Pawn";
	}

}
