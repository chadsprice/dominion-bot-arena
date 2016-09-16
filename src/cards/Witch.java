package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Witch extends Card {

	public Witch() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// each other player gains a curse
		for (Player target : targets) {
			if (game.supply.get(Card.CURSE) > 0) {
				game.gain(target, Card.CURSE);
				game.message(target, "You gain " + Card.CURSE.htmlName());
				game.messageOpponents(target, target.username + " gains " + Card.CURSE.htmlName());
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Each other player gains a Curse card."};
	}

	@Override
	public String toString() {
		return "Witch";
	}

	@Override
	public String plural() {
		return "Witches";
	}

}
