package cards;

import java.util.HashSet;
import java.util.List;

import server.Card;
import server.Duration;
import server.Game;
import server.Player;

public class Haven extends Card {

	public Haven() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public boolean onDurationPlay(Player player, Game game, List<Card> havened) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		// choose a card to haven
		if (!player.getHand().isEmpty()) {
			Card toHaven = game.promptChoosePutOnDeck(player, new HashSet<Card>(player.getHand()), "Haven: Choose a card to set aside");
			player.removeFromHand(toHaven);
			havened.add(toHaven);
			game.message(player, "setting aside " + toHaven.htmlName() + " face down");
			game.messageOpponents(player, "setting aside a card face down");
			// indicate that this haven will have an effect next turn
			return true;
		} else {
			game.message(player, "setting aside nothing because your hand is emtpy");
			game.messageOpponents(player, "setting aside nothing because his hand is emtpy");
			// indicate that this haven will have no effect next turn
			return false;
		}
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		player.addToHand(duration.havenedCards);
		game.message(player, "returning " + Card.htmlList(duration.havenedCards) + " to your hand");
		game.messageOpponents(player, "returning " + Card.numCards(duration.havenedCards.size()) + " to his hand");
		duration.havenedCards.clear();
	}
	
	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Set aside a card from your hand face down. At the start of your next turn, put it into your hand."};
	}

	@Override
	public String toString() {
		return "Haven";
	}

}
