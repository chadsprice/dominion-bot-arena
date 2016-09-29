package cards;

import server.Card;
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
		if (!game.playedSilverThisTurn && player.getPlay().contains(Card.MERCHANT)) {
			int numMerchants = player.numberInPlay(Card.MERCHANT);
			player.addCoins(numMerchants);
			game.messageAll("getting +$" + numMerchants + " because of " + Card.MERCHANT.htmlNameRaw());
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
