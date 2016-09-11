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
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		// +1 action
		player.addActions(1);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +1 action");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +1 action");
		// choose a card to haven
		if (!player.getHand().isEmpty()) {
			Card toHaven = game.promptChoosePutOnDeck(player, new HashSet<Card>(player.getHand()), "Haven: Choose a card to set aside");
			player.removeFromHand(toHaven);
			player.haven(toHaven);
			game.message(player, "... You set aside " + Card.htmlList(drawn) + " face down");
			game.messageOpponents(player, "... setting aside a card face down");
		} else {
			game.message(player, "... You set aside nothing because your hand is emtpy");
			game.message(player, "... setting aside nothing because his hand is emtpy");
		}
	}

	@Override
	public void onDurationEffect(Player player, Game game, Duration duration) {
		player.addToHand(duration.havenedCards);
		game.message(player, "... You return " + Card.htmlList(duration.havenedCards) + " to your hand");
		game.messageOpponents(player, "... returning " + duration.havenedCards + " card(s) to his hand");
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
