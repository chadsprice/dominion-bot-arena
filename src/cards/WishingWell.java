package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class WishingWell extends Card {

	public WishingWell() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		// Name a card
		List<Card> drawn = player.takeFromDraw(1);
		if (drawn.size() == 1) {
			Card namedCard = game.promptNameACard(player, "Wishing Well", "Name a card. If that is the top card of your deck, it will go into your hand");
			Card revealedCard = drawn.get(0);
			if (namedCard == revealedCard) {
				player.addToHand(revealedCard);
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it into your hand");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it into their hand");
			} else {
				player.putOnDraw(revealedCard);
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it back");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it back");
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Name a card, then reveal the top card of your deck. If you named it, put it into your hand."};
	}

	@Override
	public String toString() {
		return "Wishing Well";
	}

}
