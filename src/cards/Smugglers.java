package cards;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
			boolean canGainNothing = choosable(player, game).stream()
					.anyMatch(c -> !game.isAvailableInSupply(c));
			Card toGain;
			if (canGainNothing) {
				toGain = game.promptChooseGainFromSupply(player, smuggleable, this.toString() + ": Choose a card to gain (or gain nothing because one of the cards you may choose is not in the supply)", false, "Gain nothing");
			} else {
				toGain = game.promptChooseGainFromSupply(player, smuggleable, this.toString() + ": Choose a card to gain");
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
			game.messageAll("but " + playerOnRight.username + " gained no cards costing $6 or less on their last turn");
		}
	}

	public Set<Card> smuggleable(Player player, Game game) {
		return choosable(player, game).stream()
				.filter(game::isAvailableInSupply)
				.collect(Collectors.toSet());
	}

	private Set<Card> choosable(Player player, Game game) {
		// get the set of cards that the player to the right gained on their last turn
		List<Player> opponents = game.getOpponents(player);
		Player playerOnRight = opponents.get(opponents.size() - 1);
		Set<Card> gained = playerOnRight.cardsGainedDuringTurn;
		// get those costing $6 or less
		return gained.stream()
				.filter(c -> c.cost(game) <= 6)
				.collect(Collectors.toSet());
	}

	@Override
	public String[] description() {
		return new String[] {"Gain a copy of a card costing up to $6 that the player to your right gained on their last turn."};
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
