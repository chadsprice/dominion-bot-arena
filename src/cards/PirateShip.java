package cards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class PirateShip extends Card {

	public PirateShip() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		int choice = game.promptMultipleChoice(player, "Pirate Ship: Choose one", new String[] {"Attack", "+$1 per token"});
		if (choice == 0) {
			boolean trashedTreasure = false;
			for (Player target : targets) {
				// reveal the target's top two cards and count the distinct treasures
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
					// choose which of the two distinct treasures to trash
					Card c1 = top.get(0);
					Card c2 = top.get(1);
					int trashChoice = game.promptMultipleChoice(player, "Pirate Ship: " + target.username + " reveals " + c1.htmlName() + " and " + c2.htmlName() + ", choose one to trash", new String[] {c1.toString(), c2.toString()});
					toTrash = top.get(trashChoice);
				} else if (treasures.size() == 1) {
					toTrash = treasures.iterator().next();
				}
				if (toTrash != null) {
					game.messageAll("trashing the " + toTrash.htmlNameRaw());
					top.remove(toTrash);
					game.trash.add(toTrash);
					trashedTreasure = true;
				}
				// discard the non-treasures
				if (top.size() > 0) {
					target.addToDiscard(top);
				}
			}
			// if any target trashed a treasure
			if (trashedTreasure) {
				player.addPirateShipToken();
				game.message(player, "You place a coin token on your pirate ship mat");
				game.messageOpponents(player, player.username + " places a coin token on his pirate ship mat");
			}
		} else {
			player.addExtraCoins(player.getPirateShipTokens());
			game.messageAll("getting +$" + player.getPirateShipTokens());
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Choose one: Each other player reveals the top 2 cards his deck, trashes a revealed Treaure that you choose, discards the rest, and if anyone trashed a Treasure you take a coin token; or, +$1 per coin token you've taken with Pirate Ships this game."};
	}

	@Override
	public String toString() {
		return "Pirate Ship";
	}

}
