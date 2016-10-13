package cards;

import java.util.HashSet;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Courtyard extends Card {

	public Courtyard() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 3);
		// put a card from hand back on deck
		if (!player.getHand().isEmpty()) {
			Set<Card> choiceSet = new HashSet<Card>(player.getHand());
			Card toPutOnDeck = game.promptChoosePutOnDeck(player, choiceSet, "Courtyard: Choose a card from your hand to put on top of your deck");
			player.putFromHandOntoDraw(toPutOnDeck);
			game.message(player, "putting " + toPutOnDeck.htmlName() + " on top of your deck");
			game.messageOpponents(player, "putting a card on top of their deck");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+3 Cards", "Put a card from your hand onto your deck."};
	}

	public String toString() {
		return "Courtyard";
	}

}
