package cards;

import java.util.HashSet;
import java.util.List;

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
			Card card = game.promptChoosePassToOpponent(player, new HashSet<Card>(player.getHand()), "Ambassador: Choose a card to reveal from your hand.", "actionPrompt");
			int numInHand = 0;
			for (Card cardInHand : player.getHand()) {
				if (cardInHand == card) {
					numInHand++;
				}
			}
			String[] choices;
			if (numInHand >= 2) {
				choices = new String[] {"0", "1", "2"};
			} else {
				choices = new String[] {"0", "1"};
			}
			int numToReturn = game.promptMultipleChoice(player, "Ambassador: Choose how many to return to the supply", choices);
			for (int i = 0; i < numToReturn; i++) {
				player.removeFromHand(card);
			}
			game.returnToSupply(card, numToReturn);
			game.messageAll("returning " + card.htmlName(numToReturn) + " to the supply");
			for (Player target : targets) {
				if (game.supply.get(card) > 0) {
					game.message(target, "You gain " + card.htmlName());
					game.messageOpponents(target, target.username + " gains " + card.htmlName());
					game.gain(target, card);
				}
			}
		} else {
			game.message(player, "having no card to reveal from your hand");
			game.messageOpponents(player, "having no card to reveal from his hand");
		}
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
