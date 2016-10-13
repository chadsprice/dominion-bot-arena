package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class NativeVillage extends Card {

	public NativeVillage() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 2);
		// native village interaction
		int choice = game.promptMultipleChoice(player, "Native Village: Choose one", new String[] {"Put the top card of your deck on your native village mat", "Put all the cards from your mat into your hand"});
		if (choice == 0) {
			List<Card> drawn = player.takeFromDraw(1);
			if (drawn.size() == 1) {
				Card card = drawn.get(0);
				player.putOnNativeVillageMat(card);
				game.message(player, "putting " + card.htmlName() + " on your native village mat");
				game.messageOpponents(player, "putting the top card of their deck on their native village mat");
			} else {
				game.message(player, "putting nothing on your native village mat because your deck is empty");
				game.messageOpponents(player, "putting nothing on their native village mat because their deck is empty");
			}
		} else {
			List<Card> taken = player.takeAllFromNativeVillageMat();
			if (!taken.isEmpty()) {
				player.addToHand(taken);
				game.message(player, "putting " + Card.htmlList(taken) + " into your hand");
				game.messageOpponents(player, "putting all " + Card.numCards(taken.size()) + " from their native village mat into their hand");
			} else {
				game.message(player, "putting nothing into your hand because your native village mat is empty");
				game.messageOpponents(player, "putting nothing into their hand because their native village mat is empty");
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Actions", "Choose one: Set aside the top card of your deck face down on your Native Village mat; or put all the cards from your mat into your hand.", "You may look at the cards on your mat at any time; return them to your deck at the end of the game."};
	}

	@Override
	public String toString() {
		return "Native Village";
	}

}
