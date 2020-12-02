package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class Silver extends Card {

	@Override
	public String name() {
		return "Silver";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
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
	public String[] description() {
		return new String[]{"2$"};
	}

	@Override
	public int treasureValue() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		if (!game.playedSilverThisTurn && player.getPlay().contains(Cards.MERCHANT)) {
			int numMerchants = player.numberInPlay(Cards.MERCHANT);
			player.coins += numMerchants;
			game.messageAll("getting +$" + numMerchants + " because of " + Cards.MERCHANT.htmlNameRaw());
		}
		game.playedSilverThisTurn = true;
	}

}
