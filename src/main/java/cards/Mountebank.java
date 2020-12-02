package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import server.*;

public class Mountebank extends Card {

	@Override
	public String name() {
		return "Mountebank";
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
		return new String[] {
				"<+2$>",
				"Each other player may discard a_[Curse]. If_they_don't, they gain a_[Curse] and a_[Copper]."
		};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCoins(player, game, 2);
		for (Player target : targets) {
			// optionally discard a curse
			if (target.getHand().contains(Cards.CURSE)
					&& chooseDiscardCurse(target, game)) {
				game.message(target, "you discard " + Cards.CURSE.htmlName());
				game.messageOpponents(target, target.username + " discards " + Cards.CURSE.htmlName());
				target.putFromHandIntoDiscard(Cards.CURSE);
				continue;
			}
			// otherwise, gain a Curse and a Copper
			List<Card> toGain = new ArrayList<>();
			if (game.supply.get(Cards.CURSE) != 0) {
				toGain.add(Cards.CURSE);
			}
			if (game.supply.get(Cards.COPPER) != 0) {
				toGain.add(Cards.COPPER);
			}
			if (!toGain.isEmpty()) {
				game.message(target, "you gain " + Card.htmlList(toGain));
				game.messageOpponents(target, target.username + " gains " + Card.htmlList(toGain));
				for (Card card : toGain) {
					game.gain(target, card);
				}
			} else {
				game.message(target, "you gain nothing");
				game.messageOpponents(target, target.username + " gains nothing");
			}
		}
	}

	private boolean chooseDiscardCurse(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).mountebankDiscardCurse();
		}
		Card toDiscard = new Prompt(player, game)
				.type(Prompt.Type.DANGER)
				.message(this.toString() + ": You may discard " + Cards.CURSE + ". If you don't, you will gain " + Cards.CURSE.htmlName() + " and " + Cards.COPPER.htmlName() + ".")
				.handChoices(Collections.singleton(Cards.CURSE))
				.orNone("Don't")
				.responseCard();
		return (toDiscard != null);
	}

}
