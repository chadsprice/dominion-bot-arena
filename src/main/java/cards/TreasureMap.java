package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class TreasureMap extends Card {

	@Override
	public String name() {
		return "Treasure Map";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"Trash this and another_copy of [Treasure_Map] from your_hand.",
				"If_you_do, gain 4_[Golds], putting_them on_top of your_deck."
		};
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
		if (player.getHand().contains(Cards.TREASURE_MAP)) {
			// trash another copy of treasure map from the hand
			player.removeFromHand(Cards.TREASURE_MAP);
			game.trash(player, Cards.TREASURE_MAP);
			game.message(player, "trashing " + Cards.TREASURE_MAP.htmlName() + " from your hand");
			game.messageOpponents(player, "trashing " + Cards.TREASURE_MAP.htmlName() + " from their hand");
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

}
