package cards;

import server.Card;
import server.Duration;
import server.Game;
import server.Player;

public class MerchantShip extends Card {

	public MerchantShip() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +$2
		player.addExtraCoins(2);
		game.messageAll("getting +$2");
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		// +$2
		player.addExtraCoins(2);
		game.messageAll("getting +$2");
	}

	@Override
	public String[] description() {
		return new String[] {"Now and at the start of your next turn: +$2."};
	}

	@Override
	public String toString() {
		return "Merchant Ship";
	}

}
