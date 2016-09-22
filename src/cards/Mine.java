package cards;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Mine extends Card {

	public Mine() {
		isAction = true;
	}
	
	@Override
	public int cost() {
		return 5;
	}
	
	@Override
	public void onPlay(Player player, Game game) {
		// trash a treasure card from hand
		Set<Card> trashable = new HashSet<Card>();
		for (Card card : player.getHand()) {
			if (card.isTreasure) {
				trashable.add(card);
			}
		}
		Card toTrash = null;
		if (trashable.size() == 0) {
			// no trashable cards
			game.messageAll("trashing nothing");
			return;
		} else {
			// at least one trashable card
			Card choice = game.promptChooseTrashFromHand(player, trashable, "Mine: Choose a treasure to trash");
			if (choice != null) {
				toTrash = choice;
			} else {
				toTrash = trashable.iterator().next();
			}
		}
		int cost = toTrash.cost(game);
		// trash card
		player.removeFromHand(toTrash);
		game.trash.add(toTrash);
		game.messageAll("trashing " + toTrash.htmlName());
		
		// gain a treasure costing up to 3 more
		int maxCost = cost + 3;
		Set<Card> gainable = new HashSet<Card>();
		for (Map.Entry<Card, Integer> entry : game.supply.entrySet()) {
			Card card = entry.getKey();
			Integer count = entry.getValue();
			if (card.isTreasure && card.cost(game) <= maxCost && count > 0) {
				gainable.add(card);
			}
		}
		Card toGain = null;
		if (gainable.size() == 0) {
			// no gainable cards
			game.messageAll("gaining nothing");
			return;
		} else {
			// at least one gainable card
			Card choice = game.promptChooseGainFromSupply(player, gainable, "Mine: Choose a treasure to gain");
			if (choice != null) {
				toGain = choice;
			} else {
				toGain = gainable.iterator().next();
			}
		}
		// gain card
		game.message(player, "gaining " + toGain.htmlName() + ", putting it into your hand");
		game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it into his hand");
		game.gainToHand(player, toGain);
	}
	
	@Override
	public String[] description() {
		return new String[]{"Trash a Treasure card from your hand.", "Gain a Treasure card costing up to $3 more; put it into your hand."};
	}
	
	@Override
	public String toString() {
		return "Mine";
	}
	
}
