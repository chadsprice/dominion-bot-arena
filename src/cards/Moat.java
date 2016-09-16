package cards;

import java.util.List;

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
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
	}

	@Override
	public boolean onAttackReaction(Player player, Game game) {
		return true;
	}
	
	@Override
	public String[] description() {
		return new String[]{"+2 Cards", "When another player plays an Attack card, you may reveal this from your hand. If you do, then you are unaffected by that Attack."};
	}
	
	@Override
	public String toString() {
		return "Moat";
	}

}
