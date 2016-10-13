package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Mountebank extends Card {

	public Mountebank() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCoins(player, game, 2);
		for (Player target : targets) {
			// optionally discard a curse
			if (target.getHand().contains(Card.CURSE)) {
				int choice = game.promptMultipleChoice(target, "Mountebank: Discard " + Card.CURSE + ", or don't and gain " + Card.CURSE.htmlName() + " and " + Card.COPPER.htmlName() + "?", "attackPrompt", new String[] {"Discard a Curse", "Don't"});
				if (choice == 0) {
					game.message(target, "you discard " + Card.CURSE.htmlName());
					game.messageOpponents(target, target.username + " discards " + Card.CURSE.htmlName());
					target.putFromHandIntoDiscard(Card.CURSE);
					continue;
				}
			}
			// otherwise, gain a copper and a curse
			List<Card> toGain = new ArrayList<Card>();
			if (game.supply.get(Card.CURSE) != 0) {
				toGain.add(Card.CURSE);
			}
			if (game.supply.get(Card.COPPER) != 0) {
				toGain.add(Card.COPPER);
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

	@Override
	public String[] description() {
		return new String[] {"+$2", "Each other player may discard a Curse. If they don't, they gain a Curse and a Copper."};
	}

	@Override
	public String toString() {
		return "Mountebank";
	}

}
