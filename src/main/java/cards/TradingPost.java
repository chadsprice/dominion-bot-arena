package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class TradingPost extends Card {

	@Override
	public String name() {
		return "Trading Post";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {"Trash 2_cards from your_hand. If_you_did, gain a_[Silver] to your_hand."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		if (!player.getHand().isEmpty()) {
			List<Card> toTrash = promptTrashNumber(player, game, 2);
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
			if (toTrash.size() == 2 && game.supply.get(Cards.SILVER) > 0) {
				game.message(player, "trashing " + Card.htmlList(toTrash) + " and gaining " + Cards.SILVER.htmlName() + " to your hand");
				game.messageOpponents(player, "trashing " + Card.htmlList(toTrash) + " and gaining " + Cards.SILVER.htmlName() + " to their hand");
				game.gainToHand(player, Cards.SILVER);
			} else {
				game.message(player, "trashing " + Card.htmlList(toTrash) + " and gain nothing");
				game.messageOpponents(player, "trashing " + Card.htmlList(toTrash) + " and gaining nothing");
			}
		} else {
			game.messageAll("having no cards to trash");
		}
	}

}
