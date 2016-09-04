package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class TradingPost extends Card {

	public TradingPost() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		if (player.getHand().size() >= 2) {
			List<Card> toTrash = game.promptDiscardNumber(player, 2, true, "Trading Post", "trash");
			player.removeFromHand(toTrash);
			game.trash.addAll(toTrash);
			if (game.supply.get(Card.SILVER) > 0) {
				game.takeFromSupply(Card.SILVER);
				player.addToHand(Card.SILVER);
				game.message(player, "... You trash " + Card.htmlList(toTrash) + " and gain " + Card.SILVER.htmlName() + ", putting it into your hand");
				game.messageOpponents(player, "... trashing " + Card.htmlList(toTrash) + " and gaining " + Card.SILVER.htmlName() + ", putting it into his hand");
			} else {
				game.message(player, "... You trash " + Card.htmlList(toTrash) + " and gain nothing");
				game.messageOpponents(player, "... trashing " + Card.htmlList(toTrash) + " and gaining nothing");
			}
		} else {
			game.message(player, "... You don't have 2 cards to trash");
			game.messageOpponents(player, "... not having 2 cards to trash");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Trash 2 cards from your hand.", "If you do, gain a Silver card; put it into your hand."};
	}

	@Override
	public String toString() {
		return "Trading Post";
	}

}
