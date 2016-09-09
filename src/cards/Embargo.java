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
	public boolean onPlayWithSelfTrashing(Player player, Game game, boolean hasTrashedSelf) {
		boolean trashesSelf = false;
		// +$2
		player.addExtraCoins(2);
		game.message(player, "... You get +$2");
		game.messageOpponents(player, "... getting +$2");
		if (!hasTrashedSelf) {
			// trash this
			player.removeFromPlay(this);
			game.trash.add(this);
			game.message(player, "... You trash the " + this.htmlNameRaw());
			game.messageOpponents(player, "... trashing the " + this.htmlNameRaw());
			trashesSelf = true;
		}
		// put an embargo token on top of a supply pile
		Card toEmbargo = game.promptChooseGainFromSupply(player, game.supply.keySet(), "Embargo: Put an embargo token on a supply pile.");
		game.addEmbargoToken(toEmbargo);
		game.message(player, "... You put an embargo token on the " + toEmbargo.htmlName() + " pile");
		game.messageOpponents(player, "... putting an embargo token on the " + toEmbargo.htmlName() + " pile");
		return trashesSelf;
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