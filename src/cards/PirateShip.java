package cards;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		int choice = game.promptMultipleChoice(player, this.toString() + ": Choose one", new String[] {"Attack", "+$1 per token"});
		if (choice == 0) {
			boolean trashedTreasure = false;
			for (Player target : targets) {
				// reveal the target's top two cards and count the distinct treasures
				List<Card> top = target.takeFromDraw(2);
				if (!top.isEmpty()) {
					game.message(target, "You draw " + Card.htmlList(top));
					game.messageOpponents(target, target.username + " draws " + Card.htmlList(top));
					game.messageIndent++;
					Set<Card> treasures = top.stream()
							.filter(c -> c.isTreasure)
							.collect(Collectors.toSet());
					if (!treasures.isEmpty()) {
						Card toTrash;
						if (treasures.size() == 1) {
							toTrash = treasures.iterator().next();
						} else {
							toTrash = game.promptMultipleChoiceCard(player,
									this.toString() + ": " + target.username + " draws " + Card.htmlList(top) + ". Have them trash which one?",
									"actionPrompt",
									treasures);
						}
						game.messageAll("trashing the " + toTrash.htmlNameRaw());
						top.remove(toTrash);
						game.trash(target, toTrash);
						trashedTreasure = true;
					}
					// discard the rest
					if (!top.isEmpty()) {
						game.messageAll("discarding the rest");
						target.addToDiscard(top);
					}
					game.messageIndent--;
				} else {
					game.message(target, "Your deck is empty");
					game.messageOpponents(target, target.username + "'s deck is empty");
				}
			}
			// if any target trashed a treasure
			if (trashedTreasure) {
				game.message(player, "You place a coin token on your pirate ship mat");
				game.messageOpponents(player, player.username + " places a coin token on their pirate ship mat");
				player.addPirateShipToken();
			}
		} else {
			player.addCoins(player.getPirateShipTokens());
			game.messageAll("getting +$" + player.getPirateShipTokens());
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Choose one: Each other player reveals the top 2 cards their deck, trashes a revealed Treasure that you choose, discards the rest, and if anyone trashed a Treasure you take a coin token; or, +$1 per coin token you've taken with Pirate Ships this game."};
	}

	@Override
	public String toString() {
		return "Pirate Ship";
	}

}
