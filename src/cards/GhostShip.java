package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class GhostShip extends Card {

	public GhostShip() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 2);
		// targets discard down to 3, putting the discarded cards on top of their decks
		targets.forEach(target -> {
			if (target.getHand().size() > 3) {
				int count = target.getHand().size() - 3;
				List<Card> toPutOnDeck = game.promptPutNumberOnDeck(target, count, "Ghost Ship", "attackPrompt");
				target.removeFromHand(toPutOnDeck);
				target.putOnDraw(toPutOnDeck);
				game.message(target, "You put " + Card.numCards(toPutOnDeck.size()) + " on top of your deck");
				game.messageOpponents(target, target.username + " puts " + Card.numCards(toPutOnDeck.size()) + " on top of their deck");
			}
		});
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Each other player with 4 or more cards in hand puts cards from their hand on top of their deck until they have 3 cards in their hand."};
	}

	@Override
	public String toString() {
		return "Ghost Ship";
	}

}
