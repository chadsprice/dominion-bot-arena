package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

public class Forge extends Card {

	@Override
	public String name() {
		return "Forge";
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
		return new String[] {"Trash any_number of_cards from your_hand. Gain a_card with cost exactly_equal to the_total_cost in_coins of the_trashed_cards."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		// trash any number of cards
		List<Card> toTrash = promptTrashNumber(
				player,
                game,
                player.getHand().size(),
                Prompt.Amount.UP_TO
        );
		game.messageAll("trashing " + Card.htmlList(toTrash));
		player.removeFromHand(toTrash);
		game.trash(player, toTrash);
		// get the total cost of the trashed cards (0 if nothing was trashed)
		int totalCost = 0;
		for (Card card : toTrash) {
			totalCost += card.cost(game);
		}
		// gain a card costing exactly the total
		Set<Card> gainable = game.cardsCostingExactly(totalCost);
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
	}

}
