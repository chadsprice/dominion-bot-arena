package cards;

import server.Card;
import server.Game;
import server.Player;

public class Baron extends Card {

	public Baron() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 buy
		player.addBuys(1);
		game.messageAll("getting +1 buy");
		// may discard an estate
		boolean discardingEstate = false;
		if (player.getHand().contains(Card.ESTATE)) {
			int choice = game.promptMultipleChoice(player, "Baron: Discard " + Card.ESTATE.htmlName() + "?", new String[] {"Yes", "No"});
			discardingEstate = (choice == 0);
		}
		if (discardingEstate) {
			player.putFromHandIntoDiscard(Card.ESTATE);
			player.addExtraCoins(4);
			game.messageAll("discarding " + Card.ESTATE.htmlName() + " for +$4");
		} else {
			String cardName = null;
			if (game.supply.get(Card.ESTATE) > 0) {
				game.gain(player, Card.ESTATE);
				cardName = Card.ESTATE.htmlName();
			} else {
				cardName = "nothing";
			}
			game.messageAll("gaining " + cardName);
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "You may discard an Estate card.", "If you do, +$4.", "Otherwise, gain an Estate card."};
	}

	@Override
	public String toString() {
		return "Baron";
	}

}
