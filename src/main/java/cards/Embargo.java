package cards;

import server.*;

import java.util.Collections;
import java.util.Set;

public class Embargo extends Card {

	@Override
	public String name() {
		return "Embargo";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2$>",
				"Trash this_card. Put an_Embargo token on_top_of a_Supply_pile.",
				"When a_player buys a_card, they_gain a_[Curse] card per_Embargo token on_that_pile."
		};
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		plusCoins(player, game, 2);
		boolean movedToTrash = false;
		if (!hasMoved) {
			// trash this
			game.messageAll("trashing the " + this.htmlNameRaw());
			player.removeFromPlay(this);
			game.trash(player, this);
			movedToTrash = true;
		}
		// put an embargo token on top of a supply pile
		Object pile = chooseEmbargoPile(player, game);
		if (pile instanceof Card) {
			Card card = (Card) pile;
			game.messageAll("putting an embargo token on the " + card.htmlName() + " pile");
			game.addEmbargoToken(card);
		} else if (pile instanceof Card.MixedPileId) {
			Card.MixedPileId id = (Card.MixedPileId) pile;
			game.messageAll("putting an embargo token on the " + id.toString() + " pile");
			game.addEmbargoToken(id);
		}
		return movedToTrash;
	}

	private Object chooseEmbargoPile(Player player, Game game) {
		if (player instanceof Bot) {
			Object pile = ((Bot) player).embargoPile(Collections.unmodifiableSet(game.supply.keySet()), Collections.unmodifiableSet(game.mixedPiles.keySet()));
			if (!((pile instanceof Card && game.supply.containsKey(pile)) ||
					(pile instanceof Card.MixedPileId && game.mixedPiles.containsKey(pile)))) {
				throw new IllegalStateException();
			}
			return pile;
		}
		return promptChoosePile(
		        player,
                game,
                Prompt.Type.NORMAL,
                this.toString() + ": Put an embargo token on a supply pile.",
                null
        );
	}

}
