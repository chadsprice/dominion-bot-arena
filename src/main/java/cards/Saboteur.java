package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

public class Saboteur extends Card {

	@Override
	public String name() {
		return "Saboteur";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {"Each other player reveals cards from the_top of their_deck until revealing one costing 3$_or_more. They_trash that_card and may gain a_card costing at_most 2$_less than_it. They discard the_other revealed_cards."};
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
							Card toGain = promptChooseGainFromSupply(
									target,
									game,
									gainable,
									Prompt.Type.DANGER,
									this.toString() + ": You trash " + toTrash.htmlName() + ". You may gain a card costing at most $2 less than it.",
									"Gain nothing"
							);
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

}
