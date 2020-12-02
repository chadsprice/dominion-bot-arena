package cards;

import java.util.HashSet;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Expand extends Card {

	@Override
	public String name() {
		return "Expand";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 7;
	}

	@Override
	public String[] description() {
		return new String[]{
				"Trash a_card from your_hand.",
				"Gain a_card costing up_to 3$_more than the_trashed_card."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		// trash a card from hand
		if (!player.getHand().isEmpty()) {
			Card toTrash = promptChooseTrashFromHand(
					player,
					game,
					new HashSet<>(player.getHand()),
                    this.toString() + ": Choose a card to trash."
            );
			// trash card
			game.messageAll("trashing " + toTrash.htmlName());
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
			// gain a card costing up to 3 more
			Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 3);
			if (!gainable.isEmpty()) {
				Card toGain = promptChooseGainFromSupply(
				        player,
                        game,
                        gainable,
                        this.toString() + ": Choose a card to gain."
                );
				game.messageAll("gaining " + toGain.htmlName());
				game.gain(player, toGain);
			} else {
				game.messageAll("gaining nothing");
			}
		} else {
			game.messageAll("having no card to trash");
		}
	}

}
