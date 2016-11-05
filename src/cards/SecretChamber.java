package cards;

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
		List<Card> discarded = discardAnyNumber(player, game);
		// +$1 per card discarded
		plusCoins(player, game, discarded.size());
	}

	@Override
	public boolean onAttackReaction(Player player, Game game) {
		// +2 cards
		plusCards(player, game, 2);
		// put 2 cards from your hand back on top of your deck
		if (!player.getHand().isEmpty()) {
			List<Card> discarded = game.promptPutNumberOnDeck(player, 2, this.toString());
			game.message(player, "putting " + Card.htmlList(discarded) + " on top of your deck");
			game.messageOpponents(player, "putting " + Card.numCards(discarded.size()) + " on top of their deck");
			player.removeFromHand(discarded);
			player.putOnDraw(discarded);
		} else {
			game.message(player, "having no cards to put on top of your deck");
			game.messageOpponents(player, "having no cards to put on top of their deck");
		}
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
