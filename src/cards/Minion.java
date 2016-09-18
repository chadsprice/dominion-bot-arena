package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Minion extends Card {

	public Minion() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusActions(player, game, 1);
		// Choose effect
		int choice = game.promptMultipleChoice(player, "Minion: Choose one", new String[] {"+$2", "Discard your hand, +4 cards, and each other player with at least 5 cards in hand discards his hand and draws 4 cards"});
		if (choice == 0) {
			plusCoins(player, game, 2);
		} else {
			// discard hand and draw 4 cards
			game.messageAll("discarding " + Card.htmlList(player.getHand()));
			player.addToDiscard(player.getHand());
			player.getHand().clear();
			plusCards(player, game, 4);
			List<Player> affectedTargets = new ArrayList<Player>();
			for (Player target : targets) {
				if (target.getHand().size() >= 5) {
					affectedTargets.add(target);
				}
			}
			for (Player target : affectedTargets) {
				game.message(target, "You discard " + Card.htmlList(target.getHand()));
				game.messageOpponents(target, target.username + " discards " + Card.htmlList(target.getHand()));
				target.addToDiscard(target.getHand());
				target.getHand().clear();
				List<Card> drawn = target.drawIntoHand(4);
				game.message(target, "You draw " + Card.htmlList(drawn));
				game.messageOpponents(target, target.username + " draws " + Card.numCards(drawn.size()));
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Action", "Choose one: +$2; or discard your hand, +4 cards, and each player with at least 5 cards in hand discards his hand and draws 4 cards."};
	}

	@Override
	public String toString() {
		return "Minion";
	}

}
