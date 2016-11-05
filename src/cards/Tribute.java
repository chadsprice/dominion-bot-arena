package cards;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Tribute extends Card {

	public Tribute() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		Player playerOnLeft = game.getOpponents(player).get(0);
		List<Card> drawn = playerOnLeft.takeFromDraw(2);
		if (!drawn.isEmpty()) {
			game.message(playerOnLeft, "You draw " + Card.htmlList(drawn) + " and discard");
			game.messageOpponents(playerOnLeft, playerOnLeft.username + " draws " + Card.htmlList(drawn) + " and discards");
			playerOnLeft.addToDiscard(drawn);
			Set<Card> differentlyNamed = new HashSet<>(drawn);
			for (Card card : differentlyNamed) {
				// action -> +2 actions
				if (card.isAction) {
					game.message(player, "You get +2 actions for the " + card.htmlNameRaw());
					game.messageOpponents(player, player.username + " gets +2 actions for the " + card.htmlNameRaw());
					player.addActions(2);
				}
				// treasure -> +$2
				if (card.isTreasure) {
					game.message(player, "You get +$2 for the " + card.htmlNameRaw());
					game.messageOpponents(player, player.username + " gets +$2 for the " + card.htmlNameRaw());
					player.addCoins(2);
				}
				// victory -> +2 cards
				if (card.isVictory) {
					drawn = player.drawIntoHand(2);
					game.message(player, "You draw " + Card.htmlList(drawn) + " for the " + card.htmlNameRaw());
					game.messageOpponents(player, player.username + " draws " + Card.numCards(drawn.size()) + " for the " + card.htmlNameRaw());
				}
			}
		} else {
			game.message(playerOnLeft, "Your deck is empty");
			game.messageOpponents(playerOnLeft, playerOnLeft.username + "'s deck is empty");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"The player to your left reveals then discards the top 2 cards of their deck.", "For each differently named card revealed, if it is an...", "Action Card, +2 Actions", "Treasure Card, +$2", "Victory Card, +2 Cards"};
	}

	@Override
	public String toString() {
		return "Tribute";
	}

}
