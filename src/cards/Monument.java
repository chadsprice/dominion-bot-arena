package cards;

import server.Card;
import server.Game;
import server.Player;

public class Monument extends Card {

	public Monument() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
		plusVictoryTokens(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "+1 VP"};
	}

	@Override
	public String toString() {
		return "Monument";
	}

}
