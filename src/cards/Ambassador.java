package cards;

import java.util.HashSet;
import java.util.List;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Ambassador extends Card {

	public Ambassador() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		if (!player.getHand().isEmpty()) {
			// reveal a card
			Card revealed = game.promptChoosePassToOpponent(player, new HashSet<>(player.getHand()), "Ambassador: Choose a card to reveal from your hand.", "actionPrompt");
			if (game.canReturnToSupply(revealed)) {
				// return up to 2 of it to the supply
				int numInHand = player.numberInHand(revealed);
				int numToReturn = chooseNumToReturn(player, game, revealed);
				game.messageAll("returning " + revealed.htmlName(numToReturn) + " to the supply");
				for (int i = 0; i < numToReturn; i++) {
					player.removeFromHand(revealed);
				}
				game.returnToSupply(revealed, numToReturn);
				// each other player gains a copy
				targets.forEach(target -> {
					if (game.isAvailableInSupply(revealed)) {
						game.message(target, "You gain " + revealed.htmlName());
						game.messageOpponents(target, target.username + " gains " + revealed.htmlName());
						game.gain(target, revealed);
					}
				});
			} else {
				game.messageAll("revealing " + revealed.htmlName());
			}
		} else {
			game.message(player, "your hand is empty");
			game.messageOpponents(player, "their hand is empty");
		}
	}

	private int chooseNumToReturn(Player player, Game game, Card revealed) {
		int maximum = Math.min(player.numberInHand(revealed), 2);
		if (player instanceof Bot) {
			int numToReturn = ((Bot) player).ambassadorNumToReturn(revealed, maximum);
			if (numToReturn > maximum) {
				throw new IllegalStateException();
			}
			return numToReturn;
		}
		String[] choices = new String[maximum + 1];
		for (int i = 0; i < choices.length; i++) {
			choices[i] = i + "";
		}
		return game.promptMultipleChoice(player, "Ambassador: Choose how many to return to the supply", choices);
	}

	@Override
	public String[] description() {
		return new String[] {"Reveal a card from your hand.", "Return up to 2 copies of it from your hand to the Supply.", "Then each other player gains a copy of it."};
	}

	@Override
	public String toString() {
		return "Ambassador";
	}

}
