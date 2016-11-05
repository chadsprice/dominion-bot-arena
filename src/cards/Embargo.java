package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Embargo extends Card {

	public Embargo() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
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
			Object pile = ((Bot) player).embargoPile(game.supply.keySet(), game.mixedPiles.keySet());
			if ((pile instanceof Card && !game.supply.containsKey(pile)) ||
					(pile instanceof Card.MixedPileId && !game.mixedPiles.containsKey(pile))) {
				throw new IllegalStateException();
			}
			return pile;
		}
		return game.promptChoosePile(player, this.toString() + ": Put an embargo token on a supply pile.", "actionPrompt", true, null);
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "Trash this card. Put an Embargo token on top of a Supply pile.", "When a player buys a card, they gains a Curse card per Embargo token on that pile."};
	}

	@Override
	public String toString() {
		return "Embargo";
	}

}
