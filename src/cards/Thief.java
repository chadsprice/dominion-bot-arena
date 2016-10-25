package cards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Thief extends Card {

	public Thief() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		// each other player reveals the top 2 cards of their deck
		for (Player target : targets) {
			List<Card> top = target.takeFromDraw(2);
			game.message(target, "You reveal " + Card.htmlList(top));
			game.messageOpponents(target, target.username + " reveals " + Card.htmlList(top));
			Set<Card> treasures = new HashSet<Card>();
			for (Card card : top) {
				if (card.isTreasure) {
					treasures.add(card);
				}
			}
			Card toTrash = null;
			if (treasures.size() == 2) {
				Card c1 = top.get(0);
				Card c2 = top.get(1);
				int choice = game.promptMultipleChoice(player, "Thief: " + target.username + " reveals " + c1.htmlName() + " and " + c2.htmlName() + ", choose one to trash", new String[] {c1.toString(), c2.toString()});
				toTrash = top.get(choice);
			} else if (treasures.size() == 1) {
				toTrash = treasures.iterator().next();
			}
			if (toTrash != null) {
				game.message(target, "You trash the " + toTrash.htmlNameRaw());
				game.messageOpponents(target, target.username + " trashes the " + toTrash.htmlNameRaw());
				top.remove(toTrash);
				game.addToTrash(target, toTrash);
				int choice = game.promptMultipleChoice(player, "Thief: " + target.username + " trashes " + toTrash.htmlName() + ". Gain the trashed " + toTrash.htmlNameRaw() + "?", new String[] {"Yes", "No"});
				if (choice == 0) {
					game.message(player, "You gain the trashed " + toTrash.htmlNameRaw());
					game.messageOpponents(player, player.username + " gains the trashed " + toTrash.htmlNameRaw());
					game.gainFromTrash(player, toTrash);
				}
			}
			// discard the non-treasures
			if (!top.isEmpty()) {
				target.addToDiscard(top);
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Each other player reveals the top 2 cards of their deck.", "If they revealed any Treasure cards, they trash one of them that you choose.", "You may gain any or all of these trashed cards. They discard the other revealed cards."};
	}

	@Override
	public String toString() {
		return "Thief";
	}

	@Override
	public String plural() {
		return "Thieves";
	}

}
