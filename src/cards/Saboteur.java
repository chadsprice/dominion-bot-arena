package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Saboteur extends Card {

	public Saboteur() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		targets.forEach(target ->
			revealUntil(target, game,
					c -> c.cost(game) >= 3,
					toTrash -> {
						// trash the card costing $3 or more
						game.messageAll("trashing the " + toTrash.htmlName());
						game.trash(target, toTrash);
						// you may gain a card costing at most $2 less
						Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) - 2);
						if (!gainable.isEmpty()) {
							Card toGain = game.promptChooseGainFromSupply(target, gainable,
									this.toString() + ": You trash " + toTrash.htmlName() + ". You may gain a card costing at most $2 less than it."
									, "attackPrompt", false, "Gain nothing");
							if (toGain != null) {
								game.messageAll("gaining " + toGain.htmlName());
								game.gain(target, toGain);
							} else {
								game.messageAll("gaining nothing");
							}
						} else {
							game.messageAll("gaining nothing");
						}
					},
					true)
		);
	}

	@Override
	public String[] description() {
		return new String[] {"Each other player reveals cards from the top of their deck until revealing one costing $3 or more. They trash that card and may gain a card costing at most $2 less than it. They discard the other revealed cards."};
	}

	@Override
	public String toString() {
		return "Saboteur";
	}

}
