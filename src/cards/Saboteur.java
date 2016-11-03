package cards;

import java.util.ArrayList;
import java.util.List;

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
		for (Player target : targets) {
			// reveal cards from deck until one costs $3 or more
			Card toTrash = null;
			List<Card> revealed = new ArrayList<Card>();
			do {
				List<Card> drawn = target.takeFromDraw(1);
				if (drawn.size() == 0) {
					break;
				}
				Card card = drawn.get(0);
				revealed.add(card);
				if (card.cost(game) >= 3) {
					toTrash = card;
				}
			} while (toTrash == null);
			// if a card costing $3 or more was revealed
			if (toTrash != null) {
				// trash the card
				game.message(target, "You reveal " + Card.htmlList(revealed) + ", trashing the " + toTrash.htmlNameRaw());
				game.messageOpponents(target, target.username + " reveals " + Card.htmlList(revealed) + ", trashing the " + toTrash.htmlNameRaw());
				game.trash(target, toTrash);
				// discard the rest
				revealed.remove(toTrash);
				target.addToDiscard(revealed);
				// target may gain a card costing at most 2 less
				int cost = toTrash.cost(game) - 2;
				Card toGain = game.promptChooseGainFromSupply(target, game.cardsCostingAtMost(cost), "Saboteur: You trash " + toTrash.htmlName() + ". You may gain a card costing at most $2 less than it.", "attackPrompt", false, "Gain nothing");
				if (toGain != null) {
					game.message(target, "You gain " + toGain.htmlName());
					game.messageOpponents(target, target.username + " gains " + toGain.htmlName());
					game.gain(target, toGain);
				} else {
					game.message(target, "You gain nothing");
					game.messageOpponents(target, target.username + " gains nothing");
				}
			} else {
				if (revealed.size() > 0) {
					game.message(target, "You reveal " + Card.htmlList(revealed) + ", trashing nothing");
					game.messageOpponents(target, target.username + " reveals " + Card.htmlList(revealed) + ", trashing nothing");
				} else {
					game.message(target, "Your deck is empty");
					game.messageOpponents(target, target.username + "'s deck is empty");
				}
			}
		}
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
