package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Scout extends Card {

	public Scout() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// +1 action
		player.addActions(1);
		// reveal 4 cards, etc.
		List<Card> revealed = player.takeFromDraw(4);
		if (revealed.size() > 0) {
			// reveal top 4 cards
			game.message(player, "... You get +1 action and reveal " + Card.htmlList(revealed));
			game.messageOpponents(player, "... getting +1 action and revealing " + Card.htmlList(revealed));
			List<Card> victoryCards = removeVictoryCards(revealed);
			// add revealed victory cards to hand
			if (victoryCards.size() > 0) {
				player.addToHand(victoryCards);
				game.message(player, "... You put " + Card.htmlList(victoryCards) + " into your hand");
				game.messageOpponents(player, "... putting " + Card.htmlList(victoryCards) + " into his hand");
			}
			// put the remaining revealed cards on top of the deck
			Collections.sort(revealed, Player.HAND_ORDER_COMPARATOR);
			List<Card> toPutOnDeck = new ArrayList<Card>();
			while (revealed.size() > 0) {
				String[] choices = new String[revealed.size()];
				for (int i = 0; i < revealed.size(); i++) {
					choices[i] = revealed.get(i).toString();
				}
				int choice = game.promptMultipleChoice(player, "Scout: Put the remaining cards on top of your deck (the first card you choose will be on top of your deck)", choices);
				toPutOnDeck.add(revealed.remove(choice));
			}
			if (toPutOnDeck.size() > 0) {
				player.putOnDraw(toPutOnDeck);
				game.message(player, "... You put the remaining on top of your deck");
				game.messageOpponents(player, "... putting the remaining card(s) on top of his deck");
			}
		} else {
			game.message(player, "... Your deck is empty");
			game.messageOpponents(player, "... " + player.username + "'s deck is empty");
		}
	}

	private List<Card> removeVictoryCards(List<Card> cards) {
		List<Card> victoryCards = new ArrayList<Card>();
		Iterator<Card> iter = cards.iterator();
		while (iter.hasNext()) {
			Card card = iter.next();
			if (card.isVictory) {
				iter.remove();
				victoryCards.add(card);
			}
		}
		return victoryCards;
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Action", "Reveal the top 4 cards of your deck.", "Put the revealed Victory cards into your hand. Put the other cards on top of your deck in any order."};
	}

	@Override
	public String toString() {
		return "Scout";
	}

}
