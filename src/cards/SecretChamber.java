package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class SecretChamber extends Card {

	public SecretChamber() {
		isAction = true;
		isAttackReaction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// discard any number of cards
		List<Card> discarded = game.promptDiscardNumber(player, player.getHand().size(), false, "Secret Chamber");
		player.putFromHandIntoDiscard(discarded);
		// +$1 per card discarded
		player.addExtraCoins(discarded.size());
		game.messageAll("discarding " + Card.htmlList(discarded) + " for +$" + discarded.size());
	}

	@Override
	public boolean onAttackReaction(Player player, Game game) {
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// put 2 cards from your hand back on top of your deck
		List<Card> discarded;
		if (player.getHand().size() > 0) {
			int numToDiscard = player.getHand().size() == 1 ? 1 : 2;
			discarded = game.promptPutNumberOnDeck(player, numToDiscard, "Secret Chamber");
			player.removeFromHand(discarded);
			player.putOnDraw(discarded);
		} else {
			discarded = new ArrayList<Card>();
		}
		game.message(player, "putting " + Card.htmlList(discarded) + " on top of your deck");
		game.messageOpponents(player, "putting " + Card.numCards(drawn.size()) + " on top of his deck");
		return false;
	}

	@Override
	public String[] description() {
		return new String[] {"Discard any number of cards.", "+$1 per card discarded.", "When another player plays an Attack card, you may reveal this from your hand.", "If you do, +2 Cards, then put 2 cards from your hand on top of your deck."};
	}

	@Override
	public String toString() {
		return "Secret Chamber";
	}

}
