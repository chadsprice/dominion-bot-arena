package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Goons extends Card {

	public Goons() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusBuys(player, game, 1);
		plusCoins(player, game, 2);
		// other players discard down to 3
		for (Player target : targets) {
			if (target.getHand().size() > 3) {
				int count = target.getHand().size() - 3;
				List<Card> discarded = game.promptDiscardNumber(target, count, "Goons", "attackPrompt");
				target.putFromHandIntoDiscard(discarded);
				game.message(target, "you discard " + Card.htmlList(discarded));
				game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "+$2", "Each other player discards down to 3 cards in hand.", "While this is in play, when you buy a card, +1 VP."};
	}

	@Override
	public String toString() {
		return "Goons";
	}

}
