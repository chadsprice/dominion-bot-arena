package cards;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;

public class Smugglers extends Card {

	@Override
	public String name() {
		return "Smugglers";
	}

	@Override
	public String plural() {
		return name();
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {"Gain a copy of a_card costing up_to_6$ that the_player to_your_right gained on their last_turn."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		List<Player> opponents = game.getOpponents(player);
		Player playerOnRight = opponents.get(opponents.size() - 1);
		Set<Card> choosable = playerOnRight.cardsGainedDuringTurn.stream()
				.filter(c -> c.cost(game) <= 6)
				.collect(Collectors.toSet());
		if (!choosable.isEmpty()) {
			Set<Card> smuggleable = choosable.stream()
					.filter(game::isAvailableInSupply)
					.collect(Collectors.toSet());
			// you may choose to gain nothing if one of the cards you may choose is not available in the supply
			boolean canGainNothing = choosable.stream()
					.anyMatch(c -> !game.isAvailableInSupply(c));
			Card toGain;
			if (canGainNothing) {
				toGain = promptChooseGainFromSupply(
						player,
						game,
						smuggleable,
						this.toString() + ": Choose a card to gain (or gain nothing because one of the cards you may choose is not in the supply)",
						"Gain nothing"
				);
			} else {
				toGain = promptChooseGainFromSupply(
						player,
						game,
						smuggleable,
						this.toString() + ": Choose a card to gain"
				);
			}
			if (toGain!= null) {
				game.messageAll("gaining " + toGain.htmlName());
				game.gain(player, toGain);
			} else {
				game.messageAll("gaining nothing");
			}
		} else {
			game.messageAll("but " + playerOnRight.username + " gained no cards costing $6 or less on their last turn");
		}
	}

	public Set<Card> smuggleable(Player player, Game game) {
		List<Player> opponents = game.getOpponents(player);
		Player playerOnRight = opponents.get(opponents.size() - 1);
		return playerOnRight.cardsGainedDuringTurn.stream()
				.filter(c -> c.cost(game) <= 6)
				.filter(game::isAvailableInSupply)
				.collect(Collectors.toSet());
	}

}
