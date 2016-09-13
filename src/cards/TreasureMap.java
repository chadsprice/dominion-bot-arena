package cards;

import server.Card;
import server.Game;
import server.Player;

public class TreasureMap extends Card {

	public TreasureMap() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		boolean movedToTrash = false;
		if (!hasMoved) {
			// trash this
			player.removeFromPlay(this);
			game.trash.add(this);
			game.message(player, "... You trash the " + this.htmlNameRaw());
			game.messageOpponents(player, "... trashing the " + this.htmlNameRaw());
			movedToTrash = true;
		}
		if (player.getHand().contains(this)) {
			// trash another copy of treasure map from the hand
			player.removeFromHand(this);
			game.trash.add(this);
			game.message(player, "... You trash " + this.htmlName() + " from your hand");
			game.messageOpponents(player, "... trashing " + this.htmlName() + " from your hand");
			// only if both treasure maps were trashed
			if (movedToTrash) {
				// gain up to 4 golds, putting them on top of the deck
				int goldsToGain = Math.min(4, game.supply.get(Card.GOLD));
				for (int i = 0; i < goldsToGain; i++) {
					game.gainToTopOfDeck(player, Card.GOLD);
				}
				game.message(player, "... You gain " + Card.GOLD.htmlName(goldsToGain) + ", putting them on top of your deck");
				game.messageOpponents(player, "... gaining " + Card.GOLD.htmlName(goldsToGain) + ", putting them on top of his deck");
			}
		} else {
			game.message(player, "... You have no treasure map in hand");
			game.messageOpponents(player, "... having no treasure map in hand");
		}
		return movedToTrash;
	}

	@Override
	public String[] description() {
		return new String[] {"Trash this and another copy of Treasure Map from your hand.", "If you do trash two Treasure Maps, gain 4 Gold cards, putting them on top of your deck."};
	}

	@Override
	public String toString() {
		return "Treasure Map";
	}

}
