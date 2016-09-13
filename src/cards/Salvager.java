package cards;

import java.util.HashSet;

import server.Card;
import server.Game;
import server.Player;

public class Salvager extends Card {

	public Salvager() {
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
		game.message(player, "... You get +1 buy");
		game.messageOpponents(player, "... getting +1 buy");
		// trash a card from the hand, +$ equal to its cost
		if (!player.getHand().isEmpty()) {
			Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Salvager: Choose a card to trash for +$ equal to its cost");
			player.removeFromHand(toTrash);
			game.trash.add(toTrash);
			int cost = toTrash.cost(game);
			player.addExtraCoins(cost);
			game.message(player, "... You trash " + toTrash.htmlName() + " for +$" + cost);
			game.messageOpponents(player, "... trashing " + toTrash.htmlName() + " for +$" + cost);
		} else {
			game.message(player, "... You have no card to trash");
			game.messageOpponents(player, "... having no card to trash");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "Trash a card from your hand.", "+$ equal to its cost."};
	}

	@Override
	public String toString() {
		return "Salvager";
	}

}