package cards;

import java.util.HashSet;
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
		// get all treasures in hand
		Set<Card> treasures = treasuresInHand(player);
		if (!treasures.isEmpty()) {
			// choose a treasure to trash (or choose to trash none)
			Card toTrash = game.promptChooseTrashFromHand(player, treasures, "Mine: Choose a treasure to trash", false, "None");
			if (toTrash != null) {
				game.messageAll("trashing " + toTrash.htmlName());
				player.removeFromHand(toTrash);
				game.addToTrash(player, toTrash);
				// gain a treasure costing up to 3 more
				Set<Card> cardsCosting3More = game.cardsCostingAtMost(toTrash.cost(game) + 3);
				Set<Card> gainable = new HashSet<Card>();
				for (Card card : cardsCosting3More) {
					if (card.isTreasure) {
						gainable.add(card);
					}
				}
				if (!gainable.isEmpty()) {
					Card toGain = game.promptChooseGainFromSupply(player, gainable, "Mine: Choose a treasure to gain");
					game.message(player, "gaining " + toGain.htmlName() + " to your hand");
					game.messageOpponents(player, "gaining " + toGain.htmlName() + " to their hand");
					game.gainToHand(player, toGain);
				} else {
					game.messageAll("gaining nothing");
				}
			} else {
				game.messageAll("trashing nothing");
			}
		} else {
			game.messageAll("trashing nothing");
		}
	}
	
	private Set<Card> treasuresInHand(Player player) {
		Set<Card> treasures = new HashSet<Card>();
		for (Card card : player.getHand()) {
			if (card.isTreasure) {
				treasures.add(card);
			}
		}
		return treasures;
	}
	
	@Override
	public String[] description() {
		return new String[]{"You may trash a Treasure from your hand. Gain a Treasure to your hand costing up to $3 more than it."};
	}
	
	@Override
	public String toString() {
		return "Mine";
	}
	
}
