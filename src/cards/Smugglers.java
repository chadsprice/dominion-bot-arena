package cards;

import java.util.HashSet;
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
		Set<Card> choosable = choosable(player, game);
		if (!choosable.isEmpty()) {
			Set<Card> smuggleable = smuggleable(player, game);
			boolean canGainNothing = canGainNothing(player, game);
			Card toGain;
			if (canGainNothing) {
				toGain = game.promptChooseGainFromSupply(player, smuggleable, "Smuggler: Choose a card to gain (or gain nothing because one of the cards you may choose is not in the supply)", false, "Gain nothing");
			} else {
				toGain = game.promptChooseGainFromSupply(player, smuggleable, "Smuggler: Choose a card to gain");
			}
			if (toGain!= null) {
				game.messageAll("gaining " + toGain.htmlName());
				game.gain(player, toGain);
			} else {
				game.messageAll("gaining nothing");
			}
		} else {
			List<Player> opponents = game.getOpponents(player);
			Player playerOnRight = opponents.get(opponents.size() - 1);
			game.messageAll("but " + playerOnRight.username + " gained no cards costing $6 or less on his last turn");
		}
	}

	public Set<Card> smuggleable(Player player, Game game) {
		Set<Card> smuggleable = new HashSet<Card>();
		for (Card card : choosable(player, game)) {
			if (isInSupply(card, game)) {
				smuggleable.add(card);
			}
		}
		return smuggleable;
	}

	public boolean canGainNothing(Player player, Game game) {
		for (Card card : choosable(player, game)) {
			if (!isInSupply(card, game)) {
				return true;
			}
		}
		return false;
	}

	private Set<Card> choosable(Player player, Game game) {
		// get the set of cards that the player to the right gained on his last turn
		List<Player> opponents = game.getOpponents(player);
		Player playerOnRight = opponents.get(opponents.size() - 1);
		Set<Card> gained = playerOnRight.cardsGainedDuringTurn;
		// get those costing $6 or less
		Set<Card> choosable = new HashSet<Card>();
		for (Card card : gained) {
			if (card.cost(game) <= 6) {
				choosable.add(card);
			}
		}
		return choosable;
	}

	private boolean isInSupply(Card card, Game game) {
		Integer numInSupply = game.supply.get(card);
		return numInSupply != null && numInSupply != 0;
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
