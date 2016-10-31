package cards;

import java.util.Set;

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
			game.addToTrash(player, this);
			movedToTrash = true;
		}
		// gain a card costing up to $5
		Set<Card> gainable = game.cardsCostingAtMost(5);
		if (!gainable.isEmpty()) {
			Card toGain = game.promptChooseGainFromSupply(player, gainable, "Feast: Choose a card to gain.");
			game.messageAll("gaining " + toGain.htmlName());
			game.gain(player, toGain);
		} else {
			game.messageAll("gaining nothing");
		}
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
