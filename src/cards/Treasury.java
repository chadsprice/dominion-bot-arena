package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Treasury extends Card {

	public Treasury() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		// +1 action
		player.addActions(1);
		// +$1
		player.addExtraCoins(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + ", get +1 action, and get +$1");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s), getting +1 action, and getting +$1");
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "+$1", "When you discard this from play, if you didn't buy a Victory card this turn, you may put this on top of your deck."};
	}

	@Override
	public String toString() {
		return "Treasury";
	}

	@Override
	public String plural() {
		return "Treasuries";
	}

}
