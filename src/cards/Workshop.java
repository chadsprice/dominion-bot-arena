package cards;

import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Workshop extends Card {

	public Workshop() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// gain a card costing up to $4
		Set<Card> gainable = game.cardsCostingAtMost(4);
		if (!gainable.isEmpty()) {
			Card toGain = game.promptChooseGainFromSupply(player, gainable, "Workshop: Choose a card to gain.");
			game.messageAll("gaining " + toGain.htmlName());
			game.gain(player, toGain);
		} else {
			game.messageAll("gaining nothing");
		}
	}

	@Override
	public String[] description() {
		return new String[]{"Gain a card costing up to $4."};
	}

	@Override
	public String toString() {
		return "Workshop";
	}

}
