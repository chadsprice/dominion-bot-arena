package cards;

import server.Card;
import server.Game;
import server.Player;

public class Coppersmith extends Card {

	public Coppersmith() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		game.coppersmithsPlayedThisTurn++;
		player.sendCoins();
		for (Player eachPlayer : game.players) {
			game.message(eachPlayer, "... Copper produces an extra $1 this turn");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Copper produces an extra $1 this turn."};
	}

	@Override
	public String toString() {
		return "Coppersmith";
	}

}
