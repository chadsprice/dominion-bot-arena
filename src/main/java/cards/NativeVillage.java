package cards;

import java.util.List;
import java.util.Set;

import server.*;

public class NativeVillage extends Card {

	@Override
	public String name() {
		return "Native Village";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2_Actions>",
				"Choose_one:",
				"* Set aside the top card of your_deck face_down on your Native_Village mat",
				"* Put all the cards from your_mat into your_hand.",
				"You may look at the cards on your mat at_any_time. Return_them to your_deck at the end of the_game."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusActions(player, game, 2);
		// native village interaction
		if (choosePutOverTake(player, game)) {
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

	private boolean choosePutOverTake(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).nativeVillagePutOverTake();
		}
		int choice = new Prompt(player, game)
				.message(this.toString() + ": Choose one")
				.multipleChoices(new String[] {"Put the top card of your deck on your native village mat", "Put all the cards from your mat into your hand"})
				.responseMultipleChoiceIndex();
		return (choice == 0);
	}

}
