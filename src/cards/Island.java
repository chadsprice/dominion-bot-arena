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
			game.message(player, "putting the " + this.htmlNameRaw() + " on your island mat");
			game.messageOpponents(player, "putting the " + this.htmlNameRaw() + " on their island mat");
			movedToIsland = true;
		}
		if (!player.getHand().isEmpty()) {
			Card card = game.promptChooseIslandFromHand(player, new HashSet<>(player.getHand()), "Island: Choose a card to set aside on your island mat.");
			player.removeFromHand(card);
			player.putOnIslandMat(card);
			game.message(player, "putting " + card.htmlName() + " on your island mat");
			game.messageOpponents(player, "putting " + card.htmlName() + " on their island mat");
		} else {
			game.message(player, "putting nothing else on your island mat because your hand is empty");
			game.messageOpponents(player, "putting nothing else on their island mat because their hand is empty");
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
