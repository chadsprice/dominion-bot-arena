package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Torturer extends Card {

	public Torturer() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		// +3 cards
		List<Card> drawn = player.drawIntoHand(3);
		game.message(player, "... You draw " + Card.htmlList(drawn));
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s)");
		// torture targets
		for (Player target : targets) {
			int choice = game.promptMultipleChoice(target, "Torturer: Choose one", "attackPrompt", new String[] {"Discard 2 cards", "Gain a Curse, putting it into your hand"});
			if (choice == 0) {
				// discard 2
				if (target.getHand().size() > 0) {
					List<Card> toDiscard = game.promptDiscardNumber(target, 2, "Torturer", "attackPrompt");
					target.putFromHandIntoDiscard(toDiscard);
					game.message(target, "... You discard " + Card.htmlList(toDiscard));
					game.messageOpponents(target, "... " + target.username + " discards " + Card.htmlList(toDiscard));
				} else {
					game.message(target, "... You reveal an empty hand, discarding nothing");
					game.messageOpponents(target, "... " + target.username + " reveals an empty hand, discarding nothing");
				}
			} else {
				// gain a curse putting it into your hand
				if (game.supply.get(Card.CURSE) > 0) {
					game.takeFromSupply(Card.CURSE);
					target.addToHand(Card.CURSE);
					game.message(target, "... You gain " + Card.CURSE.htmlName() + ", putting it into your hand");
					game.messageOpponents(target, "... " + target.username + " gains " + Card.CURSE.htmlName() + ", putting it into his hand");
				} else {
					game.message(target, "... You gain nothing");
					game.messageOpponents(target, "... " + target.username + " gains nothing");
				}
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+3 Cards", "Each other player chooses one: he discards 2 cards; or he gains a Curse card, putting it in his hand."};
	}

	@Override
	public String toString() {
		return "Torturer";
	}

}
