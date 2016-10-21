package cards;

import server.Card;
import server.Game;
import server.Player;

public class Chancellor extends Card {

	public Chancellor() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
		// you may immediately put your deck into your discard pile
		if (!player.getDraw().isEmpty()) {
			int choice = game.promptMultipleChoice(player, "Chancellor: Put your deck into your discard pile immediately?", new String[]{"Yes", "No"});
			if (choice == 0) {
				game.message(player, "putting your deck into your discard pile immediately");
				game.messageOpponents(player, "putting their deck into their discard pile immediately");
				// this does not trigger the Tunnel reaction
				player.addToDiscard(player.takeFromDraw(player.getDraw().size()), false);
			}
		}
	}

	@Override
	public String[] description() {
		return new String[]{"+$2", "You may immediately put your deck into your discard pile."};
	}
	
	@Override
	public String toString() {
		return "Chancellor";
	}

}
