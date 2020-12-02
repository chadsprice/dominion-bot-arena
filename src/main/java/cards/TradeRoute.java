package cards;

import java.util.HashSet;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class TradeRoute extends Card {

	@Override
	public String name() {
		return "Trade Route";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Buy>",
				"<+1$> per token on the Trade_Route_mat.",
				"Trash a card from your_hand.",
				"Setup: Put a token on each Victory_card Supply_pile. When a_card is_gained from that_pile, move the_token to the Trade_Route_mat."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		if (game.tradeRouteMat > 0) {
			plusCoins(player, game, game.tradeRouteMat);
		}
		if (!player.getHand().isEmpty()) {
			Card toTrash = promptChooseTrashFromHand(
					player,
					game,
					new HashSet<>(player.getHand()),
					this.toString() + ": Choose a card to trash from your hand."
			);
			game.messageAll("trashing " + toTrash.htmlName());
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
		} else {
			game.messageAll("having no card to trash");
		}
	}

}
