package cards;

import java.util.ArrayList;
import java.util.List;

import server.Card;
import server.Duration;
import server.Game;
import server.Player;

public class Tactician extends Card {

	public Tactician() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public boolean onDurationPlay(Player player, Game game, List<Card> havenedCards) {
		if (!player.getHand().isEmpty()) {
			List<Card> allCardsInHand = new ArrayList<Card>(player.getHand());
			player.putFromHandIntoDiscard(allCardsInHand);
			game.messageAll("discarding " + Card.htmlList(allCardsInHand));
			return true;
		} else {
			game.message(player, "but your hand is empty");
			game.messageOpponents(player, "but his hand is empty");
			return false;
		}
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		// +5 cards
		List<Card> drawn = player.drawIntoHand(5);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// +1 buy
		player.addBuys(1);
		game.messageAll("getting +1 buy");
		// +1 action
		player.addActions(1);
		game.messageAll("getting +1 action");
	}

	@Override
	public String[] description() {
		return new String[] {"Discard your hand.", "If you discarded any cards this way, then at the start of your next turn, +5 Cards, +1 Buy, and +1 Action."};
	}

	@Override
	public String toString() {
		return "Tactician";
	}

}
