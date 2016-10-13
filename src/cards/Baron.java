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
		plusBuys(player, game, 1);
		// may discard an estate
		boolean discardingEstate = false;
		if (player.getHand().contains(Card.ESTATE)) {
			int choice = game.promptMultipleChoice(player, "Baron: Discard " + Card.ESTATE.htmlName() + " for +$4?", new String[] {"Yes", "No"});
			if (choice == 0) {
				discardingEstate = true;
			}
		}
		if (discardingEstate) {
			game.messageAll("discarding " + Card.ESTATE.htmlName() + " for +$4");
			player.putFromHandIntoDiscard(Card.ESTATE);
			player.addCoins(4);
		} else {
			if (game.supply.get(Card.ESTATE) > 0) {
				game.messageAll("gaining " + Card.ESTATE.htmlName());
				game.gain(player, Card.ESTATE);
			} else {
				game.messageAll("gaining nothing");
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "You may discard an Estate for +$4. If you don't, gain an Estate."};
	}

	@Override
	public String toString() {
		return "Baron";
	}

}
