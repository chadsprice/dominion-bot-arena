package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Vault extends Card {

	public Vault() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
		// discard any number of cards
		List<Card> discarded = game.promptDiscardNumber(player, player.getHand().size(), false, "Vault");
		player.putFromHandIntoDiscard(discarded);
		// +$1 per card discarded
		player.addCoins(discarded.size());
		game.messageAll("discarding " + Card.htmlList(discarded) + " for +$" + discarded.size());
		// each other player may discard 2 cards to draw 1
		for (Player other : game.getOpponents(player)) {
			if (other.getHand().size() >= 2) {
				int choice = game.promptMultipleChoice(other, "Vault: Discard 2 cards and draw 1 card?", new String[] {"Yes", "No"});
				if (choice == 0) {
					List<Card> toDiscard = game.promptDiscardNumber(other, 2, true, "Vault");
					game.message(other, "you discard " + Card.htmlList(toDiscard));
					game.messageOpponents(other, other.username + " discards " + Card.htmlList(toDiscard));
					other.putFromHandIntoDiscard(toDiscard);
					game.messageIndent++;
					plusCards(other, game, 1);
					game.messageIndent--;
				}
			} else if (other.getHand().size() == 1) {
				int choice = game.promptMultipleChoice(other, "Vault: Discard your only card? (you will not get to draw a card)", new String[] {"Yes", "No"});
				if (choice == 0) {
					Card toDiscard = other.getHand().get(0);
					game.message(other, "you discard " + toDiscard.htmlName());
					game.messageOpponents(other, other.username + " discards " + toDiscard.htmlName());
					other.putFromHandIntoDiscard(toDiscard);
				}
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Discard any number of cards.", "+$1 per card discarded.", "Each other player may discard 2 cards. If they do, they draw a card."};
	}

	@Override
	public String toString() {
		return "Vault";
	}

}
