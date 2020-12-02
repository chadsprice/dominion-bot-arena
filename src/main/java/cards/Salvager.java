package cards;

import java.util.HashSet;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Salvager extends Card {

	@Override
	public String name() {
		return "Salvager";
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
				"<+1_Buy>",
				"Trash a card from your_hand.",
				"<+$> equal to its_cost."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		// trash a card from the hand, +$ equal to its cost
		if (!player.getHand().isEmpty()) {
			Card toTrash = promptChooseTrashFromHand(
					player,
					game,
					new HashSet<>(player.getHand()),
					this.toString() + ": Choose a card to trash for +$ equal to its cost"
			);
			int cost = toTrash.cost(game);
			game.messageAll("trashing " + toTrash.htmlName() + " for +$" + cost);
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
			player.coins += cost;
		} else {
			game.messageAll("having no card to trash");
		}
	}

}
