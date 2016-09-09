package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class PearlDiver extends Card {

	public PearlDiver() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		// +1 action
		player.addActions(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 action");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 action");
		// look at bottom of deck
		Card card = player.bottomOfDeck();
		if (card != null) {
			int choice = game.promptMultipleChoice(player, "Pearl Diver: You see " + card.htmlName() + ". Put it on top of your deck?", new String[] {"Put it on top", "Leave it on bottom"});
			if (choice == 0) {
				player.putOnDraw(player.takeFromBottomOfDeck());
				game.message(player, "... You put the " + card.htmlNameRaw() + " on top of your deck");
				game.messageOpponents(player, "... putting the card at the bottom of his deck on top");
			} else {
				game.message(player, "... You leave the " + card.htmlNameRaw() + " at the bottom of your deck");
				game.messageOpponents(player, "... leaving the card at the bottom of his deck");
			}
		} else {
			game.message(player, "... Your deck is empty");
			game.messageOpponents(player, "... having an empty deck");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Look at the bottom card of your deck. You may put it on top."};
	}

	@Override
	public String toString() {
		return "Pearl Diver";
	}

}
