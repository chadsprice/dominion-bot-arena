package cards;

import java.util.HashSet;

import server.Card;
import server.Game;
import server.Player;

public class TradeRoute extends Card {

	public TradeRoute() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		if (game.tradeRouteMat > 0) {
			plusCoins(player, game, game.tradeRouteMat);
		}
		if (!player.getHand().isEmpty()) {
			Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Trade Route: Choose a card to trash from your hand.");
			game.messageAll("trashing " + toTrash.htmlName());
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
		} else {
			game.messageAll("having no card to trash");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "+$1 per token on the Trade Route mat.", "Trash a card from your hand.", "Setup: Put a token on each Victory card Supply pile. When a card is gained from that pile, move the token to the Trade Route mat."};
	}

	@Override
	public String toString() {
		return "Trade Route";
	}

}
