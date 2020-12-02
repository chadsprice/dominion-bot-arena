package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Feast extends Card {

	@Override
	public String name() {
		return "Feast";
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
		return new String[]{"Trash this_card.", "Gain a_card costing up_to_5$."};
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

}
