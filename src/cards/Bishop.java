package cards;

import java.util.HashSet;

import server.Card;
import server.Game;
import server.Player;

public class Bishop extends Card {

	public Bishop() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 1);
		plusVictoryTokens(player, game, 1);
		// trash a card for +VP equal to half its cost in coins rounded down
		if (!player.getHand().isEmpty()) {
			Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()), "Bishop: Choose a card to trash for +VP equal to half its cost rounded down.");
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
			int victoryTokensToGain = toTrash.cost(game) / 2;
			player.addVictoryTokens(victoryTokensToGain);
			game.messageAll("trashing " + toTrash.htmlName() + " for +" + victoryTokensToGain + " VP");
		} else {
			game.messageAll("having no card to trash");
		}
		// each other player may trash a card
		game.getOpponents(player).forEach(opponent -> {
			if (!opponent.getHand().isEmpty()) {
				int choice = game.promptMultipleChoice(opponent, "Bishop: Trash a card from your hand?", new String[] {"Yes", "No"});
				if (choice == 0) {
					Card toTrash = game.promptChooseTrashFromHand(opponent, new HashSet<>(player.getHand()), "Bishop: Choose a card to trash.");
					opponent.removeFromHand(toTrash);
					game.trash(opponent, toTrash);
					game.message(opponent, "you trash " + toTrash.htmlName());
					game.messageOpponents(opponent, opponent.username + " trashes " + toTrash.htmlName());
				}
			}
		});
	}

	@Override
	public String[] description() {
		return new String[] {"+$1", "+1 VP", "Trash a card from your hand.", "+VP equal to half its cost in coins rounded down.", "Each other player may trash a card from their hand."};
	}

	@Override
	public String toString() {
		return "Bishop";
	}

}
