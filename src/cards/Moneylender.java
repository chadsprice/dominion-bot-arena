package cards;

import server.Card;
import server.Game;
import server.Player;

public class Moneylender extends Card {

	public Moneylender() {
		isAction = true;
	}
	
	@Override
	public int cost() {
		return 4;
	}
	
	@Override
	public void onPlay(Player player, Game game) {
		// if player has a Copper in his hand
		if (player.getHand().contains(Card.COPPER)) {
			// trash it
			player.removeFromHand(Card.COPPER);
			game.trash.add(Card.COPPER);
			// +$3
			player.addExtraCoins(3);
			game.messageAll("trashing " + Card.COPPER.htmlName() + " for +$3");
		} else {
			game.messageAll("trashing nothing");
		}
	}
	
	@Override
	public String[] description() {
		return new String[] {"Trash a Copper from your hand.", "If you do, +$3."};
	}
	
	@Override
	public String toString() {
		return "Moneylender";
	}
	
}
