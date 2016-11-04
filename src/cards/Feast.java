package cards;

import server.Card;
import server.Game;
import server.Player;

public class Feast extends Card {

	public Feast() {
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
			game.messageAll("trashing the " + this.htmlNameRaw());
			player.removeFromPlay(this);
			game.trash(player, this);
			movedToTrash = true;
		}
		// gain a card costing up to $5
		gainCardCostingUpTo(player, game, 5);
		return movedToTrash;
	}

	@Override
	public String[] description() {
		return new String[]{"Trash this card.", "Gain a card costing up to $5."};
	}

	public String toString() {
		return "Feast";
	}

}
