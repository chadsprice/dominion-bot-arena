package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Forge extends Card {

	public Forge() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 7;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// trash any number of cards
		List<Card> toTrash = game.promptTrashNumber(player, player.getHand().size(), false, "Forge");
		game.messageAll("trashing " + Card.htmlList(toTrash));
		player.removeFromHand(toTrash);
		game.addToTrash(player, toTrash);
		// get the total cost of the trashed cards (0 if nothing was trashed)
		int totalCost = 0;
		for (Card card : toTrash) {
			totalCost += card.cost(game);
		}
		// gain a card costing exactly the total
		Set<Card> gainable = game.cardsCostingExactly(totalCost);
		if (!gainable.isEmpty()) {
			Card toGain = game.promptChooseGainFromSupply(player, gainable, "Forge: Choose a card to gain.");
			game.messageAll("gaining " + toGain.htmlName());
			game.gain(player, toGain);
		} else {
			game.messageAll("gaining nothing");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Trash any number of cards from your hand. Gain a card with cost exactly equal to the total cost in coins of the trashed cards."};
	}

	@Override
	public String toString() {
		return "Forge";
	}

}
