package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Smugglers extends Card {

	public Smugglers() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// get the set of cards that the player to the right gained on his last turn
		List<Player> opponents = game.getOpponents(player);
		Player playerOnRight = opponents.get(opponents.size() - 1);
		Set<Card> gained = playerOnRight.cardsGainedDuringTurn;
		// get those costing $6 or less
		List<Card> smuggleable = new ArrayList<Card>();
		for (Card card : gained) {
			if (card.cost(game) <= 6) {
				smuggleable.add(card);
			}
		}
		if (!smuggleable.isEmpty()) {
			// sort them and choose one to gain
			Collections.sort(smuggleable, Player.HAND_ORDER_COMPARATOR);
			String[] choices = new String[smuggleable.size()];
			for (int i = 0; i < smuggleable.size(); i++) {
				choices[i] = smuggleable.get(i).toString();
			}
			int choice = game.promptMultipleChoice(player, "Smugglers: Choose a card to gain (if there are any in the supply)", choices);
			Card toGain = smuggleable.get(choice);
			// if there are any of the chosen card to gain in the supply
			if (game.supply.get(toGain) != null && game.supply.get(toGain) > 0) {
				game.gain(player, toGain);
				game.messageAll("gaining " + toGain.htmlName());
			} else {
				game.messageAll("gaining nothing");
			}
		} else {
			game.messageAll("but " + playerOnRight.username + " gained no cards costing $6 or less on his last turn");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Gain a copy of a card costing up to $6 that the player to your right gained on his last turn."};
	}

	@Override
	public String toString() {
		return "Smugglers";
	}

	@Override
	public String plural() {
		return "Smugglers";
	}

}
