package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Smithy extends Card {

	public Smithy() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +3 cards
		List<Card> drawn = player.drawIntoHand(3);
		game.message(player, "... You draw " + Card.htmlList(drawn));
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s)");
	}

	@Override
	public String[] description() {
		return new String[]{"+3 Cards"};
	}

	@Override
	public String toString() {
		return "Smithy";
	}

	@Override
	public String plural() {
		return "Smithies";
	}

}
