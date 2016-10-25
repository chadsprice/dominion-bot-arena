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
		boolean trashingCopper = false;
		// if you have a Copper in hand
		if (player.getHand().contains(Card.COPPER)) {
			// you may choose to trash it for +$3
			int choice = game.promptMultipleChoice(player, "Moneylender: Trash " + Card.COPPER.htmlName() + " for +$3?", new String[] {"Yes", "No"});
			if (choice == 0) {
				trashingCopper = true;
			}
		}
		if (trashingCopper) {
			game.messageAll("trashing " + Card.COPPER.htmlName() + " for +$3");
			player.removeFromHand(Card.COPPER);
			game.addToTrash(player, Card.COPPER);
			player.addCoins(3);
		} else {
			game.messageAll("trashing nothing");
		}
	}
	
	@Override
	public String[] description() {
		return new String[] {"You may trash a Copper from your hand for +$3."};
	}
	
	@Override
	public String toString() {
		return "Moneylender";
	}
	
}
