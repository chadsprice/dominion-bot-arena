package cards;

import server.Card;
import server.Game;
import server.Player;

public class Moat extends Card {

	public Moat() {
		isAction = true;
		isAttackReaction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
	}

	@Override
	public boolean onAttackReaction(Player player, Game game) {
		return true;
	}
	
	@Override
	public String[] description() {
		return new String[]{"+2 Cards", "When another player plays an Attack card, you may first reveal this from your hand, to be unaffected by it."};
	}
	
	@Override
	public String toString() {
		return "Moat";
	}

}
