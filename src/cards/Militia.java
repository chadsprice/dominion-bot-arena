package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Militia extends Card {

	public Militia() {
		isAction = true;
		isAttack = true;
	}
	
	@Override
	public int cost() {
		return 4;
	}
	
	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCoins(player, game, 2);
		// each other player discards down to 3
		handSizeAttack(targets, game, 3);
	}
	
	@Override
	public String[] description() {
		return new String[]{"+$2", "Each other player discards down to 3 cards in hand."};
	}
	
	@Override
	public String toString() {
		return "Militia";
	}
	
}
