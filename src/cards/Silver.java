package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class Silver extends Card {

	public Silver() {
		isTreasure = true;
	}

	@Override
	public int startingSupply(int numPlayers) {
		return 40;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public int treasureValue() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		if (!game.playedSilverThisTurn && player.getPlay().contains(Cards.MERCHANT)) {
			int numMerchants = player.numberInPlay(Cards.MERCHANT);
			player.addCoins(numMerchants);
			game.messageAll("getting +$" + numMerchants + " because of " + Cards.MERCHANT.htmlNameRaw());
		}
		game.playedSilverThisTurn = true;
	}

	@Override
	public String[] description() {
		return new String[]{"$2"};
	}

	@Override
	public String toString() {
		return "Silver";
	}

}
