package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class SecretChamber extends Card {

	@Override
	public String name() {
		return "Secret Chamber";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK_REACTION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[] {
				"Discard any_number of_cards.",
				"<+1$>_per card discarded.",
				"When another player plays an_Attack card, you_may reveal_this from your_hand.",
				"If_you_do, <+2_Cards>, then_put 2_cards from your_hand on_top of your_deck."
		};
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
			List<Card> discarded = promptPutNumberOnDeck(player, game, 2);
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

}
