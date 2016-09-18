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
		for (Player target : targets) {
			if (target.getHand().size() > 3) {
				int count = target.getHand().size() - 3;
				List<Card> toPutOnDeck = game.promptPutNumberOnDeck(target, count, "Ghost Ship", "attackPrompt");
				target.removeFromHand(toPutOnDeck);
				target.putOnDraw(toPutOnDeck);
				game.message(target, "You put " + Card.numCards(toPutOnDeck.size()) + " on top of your deck");
				game.messageOpponents(target, target.username + " puts " + Card.numCards(toPutOnDeck.size()) + " on top of his deck");
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Each other player with 4 or more cards in hand puts cards from his hand on top of his deck until he has 3 cards in his hand."};
	}

	@Override
	public String toString() {
		return "Ghost Ship";
	}

}
