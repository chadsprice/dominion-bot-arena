package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Spy extends Card {

	public Spy() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		// reveal top card of each player's deck and decide to keep it or discard it
		targets.add(0, player);
		for (Player target : targets) {
			List<Card> top = target.takeFromDraw(1);
			if (top.size() == 1) {
				Card card = top.get(0);
				game.message(target, "You reveal " + card.htmlName());
				game.messageOpponents(target, target.username + " reveals " + card.htmlName());
				int choice;
				if (target == player) {
					choice = game.promptMultipleChoice(player, "Spy: You reveal " + card.htmlName(), new String[] {"Keep it", "Discard it"});
				} else {
					choice = game.promptMultipleChoice(player, "Spy: " + target.username + " reveals " + card.htmlName(), new String[] {"He keeps it", "He discards it"});
				}
				if (choice == 0) {
					target.putOnDraw(card);
					game.message(target, "You put it back");
					game.messageOpponents(target, target.username + " puts it back");
				} else {
					target.addToDiscard(card);
					game.message(target, "You discard it");
					game.messageOpponents(target, target.username + " discards it");
				}
			} else {
				game.message(target, "Your deck is empty");
				game.messageOpponents(target, target.username + "'s deck is empty");
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Each player (including you) reveals the top card of his deck and either discards it or puts it back, your choice."};
	}

	@Override
	public String toString() {
		return "Spy";
	}

	@Override
	public String plural() {
		return "Spies";
	}

}
