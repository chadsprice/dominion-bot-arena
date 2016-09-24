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
		// trash a treasure card from hand
		Set<Card> treasures = treasuresInHand(player);
		if (!treasures.isEmpty()) {
			Card toTrash = game.promptChooseTrashFromHand(player, treasures, "Mine: Choose a treasure to trash");
			game.messageAll("trashing " + toTrash.htmlName());
			player.removeFromHand(toTrash);
			game.trash.add(toTrash);
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
				game.message(player, "gaining " + toGain.htmlName() + ", putting it into your hand");
				game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it into his hand");
				game.gainToHand(player, toGain);
			} else {
				game.messageAll("gaining nothing");
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
		return new String[]{"Trash a Treasure card from your hand.", "Gain a Treasure card costing up to $3 more; put it into your hand."};
	}
	
	@Override
	public String toString() {
		return "Mine";
	}
	
}
