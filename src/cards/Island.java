package cards;

import java.util.HashSet;

import server.Card;
import server.Game;
import server.Player;

public class Island extends Card {

	public Island() {
		isAction = true;
		isVictory = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public int victoryValue() {
		return 2;
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		boolean movedToIsland = false;
		if (!hasMoved) {
			// move this to the island mat
			player.removeFromPlay(this);
			player.putOnIslandMat(this);
			game.message(player, "... You put the " + this.htmlNameRaw() + " on your island mat");
			game.messageOpponents(player, "... putting the " + this.htmlNameRaw() + " on his island mat");
			movedToIsland = true;
		}
		if (!player.getHand().isEmpty()) {
			Card card = game.promptChooseIslandFromHand(player, new HashSet<Card>(player.getHand()), "Island: Choose a card to set aside on your island mat");
			player.removeFromHand(card);
			player.putOnIslandMat(card);
			game.message(player, "... You put " + card.htmlName() + " on your island mat");
			game.messageOpponents(player, "... putting " + card.htmlName() + " on his island mat");
		} else {
			game.message(player, "... You have no cards to put on your island mat");
			game.messageOpponents(player, "... having no cards to put on his island mat");
		}
		return movedToIsland;
	}

	@Override
	public String[] description() {
		return new String[] {"Set aside this and another card from your hand. Return them to your deck at the end of the game.", "2 VP"};
	}

	@Override
	public String toString() {
		return "Island";
	}

}
