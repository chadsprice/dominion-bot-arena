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
		// +1 buy
		player.addBuys(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 buy");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 buy");
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		// +1 buy
		player.addBuys(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 buy");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 buy");
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
