package cards;

import java.util.ArrayList;
import java.util.List;

import server.Bot;
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
		// choose +$2 or attack
		if (chooseCoinOverAttack(player, game)) {
			plusCoins(player, game, 2);
		} else {
			// discard hand and draw 4 cards
			game.messageAll("discarding " + Card.htmlList(player.getHand()));
			player.putFromHandIntoDiscard(new ArrayList<>(player.getHand()));
			plusCards(player, game, 4);
			// each other player with at least 5 cards discard their hand and draws 4 cards
			targets.forEach(target -> {
				if (target.getHand().size() >= 5) {
					game.message(target, "You discard " + Card.htmlList(target.getHand()));
					game.messageOpponents(target, target.username + " discards " + Card.htmlList(target.getHand()));
					target.putFromHandIntoDiscard(new ArrayList<>(target.getHand()));
					game.messageIndent++;
					plusCards(target, game, 4);
					game.messageIndent--;
				}
			});
		}
	}

	private boolean chooseCoinOverAttack(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).minionCoinOverAttack();
		}
		int choice = game.promptMultipleChoice(player, this.toString() + ": Choose one", new String[] {"+$2", "Attack"});
		return (choice == 0);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Action", "Choose one: +$2; or discard your hand, +4 cards, and each player with at least 5 cards in hand discards their hand and draws 4 cards."};
	}

	@Override
	public String toString() {
		return "Minion";
	}

}
