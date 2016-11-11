package cards;

import server.Card;
import server.Cards;
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
			game.trash(player, this);
			game.messageAll("trashing the " + this.htmlNameRaw());
			movedToTrash = true;
		}
		if (player.getHand().contains(this)) {
			// trash another copy of treasure map from the hand
			player.removeFromHand(this);
			game.trash(player, this);
			game.message(player, "trashing " + this.htmlName() + " from your hand");
			game.messageOpponents(player, "trashing " + this.htmlName() + " from their hand");
			// only if both treasure maps were trashed
			if (movedToTrash) {
				// gain up to 4 golds, putting them on top of your deck
				int goldsToGain = Math.min(4, game.supply.get(Cards.GOLD));
				game.message(player, "gaining " + Cards.GOLD.htmlName(goldsToGain) + ", putting them on top of your deck");
				game.messageOpponents(player, "gaining " + Cards.GOLD.htmlName(goldsToGain) + ", putting them on top of their deck");
				for (int i = 0; i < 4 && game.supply.get(Cards.GOLD) != 0; i++) {
					game.gainToTopOfDeck(player, Cards.GOLD);
				}
			}
		} else {
			game.messageAll("having no treasure map in hand");
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
