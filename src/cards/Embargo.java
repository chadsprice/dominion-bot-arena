package cards;

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
		boolean movedToTrash = false;
		plusCoins(player, game, 2);
		if (!hasMoved) {
			// trash this
			player.removeFromPlay(this);
			game.trash.add(this);
			game.messageAll("trashing the " + this.htmlNameRaw());
			movedToTrash = true;
		}
		// put an embargo token on top of a supply pile
		Card toEmbargo = game.promptChooseGainFromSupply(player, game.supply.keySet(), "Embargo: Put an embargo token on a supply pile.");
		game.addEmbargoToken(toEmbargo);
		game.messageAll("putting an embargo token on the " + toEmbargo.htmlName() + " pile");
		return movedToTrash;
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "Trash this card. Put an Embargo token on top of a Supply pile.", "When a player buys a card, he gains a Curse card per Embargo token on that pile."};
	}

	@Override
	public String toString() {
		return "Embargo";
	}

}
