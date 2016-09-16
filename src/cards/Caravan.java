package cards;

import java.util.List;

import server.Card;
import server.Duration;
import server.Game;
import server.Player;

public class Caravan extends Card {

	public Caravan() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// +1 action
		player.addActions(1);
		game.messageAll("getting +1 action");
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "At the start of your next turn, +1 Card."};
	}

	@Override
	public String toString() {
		return "Caravan";
	}

}
