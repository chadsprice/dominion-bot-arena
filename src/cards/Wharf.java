package cards;

import java.util.List;

import server.Card;
import server.Duration;
import server.Game;
import server.Player;

public class Wharf extends Card {

	public Wharf() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// +1 buy
		player.addBuys(1);
		game.messageAll("getting +1 buy");
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// +1 buy
		player.addBuys(1);
		game.messageAll("getting +1 buy");
	}

	@Override
	public String[] description() {
		return new String[] {"Now and at the start of your next turn:", "+2 Cards", "+1 Buy"};
	}

	@Override
	public String toString() {
		return "Wharf";
	}

}
