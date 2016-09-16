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
		// +$2
		player.addExtraCoins(2);
		game.messageAll("getting +$2");
		// you may immediately put your deck into your discard pile
		if (player.getDraw().size() > 0) {
			int choice = game.promptMultipleChoice(player, "Chancellor: Put your deck into your discard pile immediately?", new String[]{"Yes", "No"});
			if (choice == 0) {
				player.addToDiscard(player.takeFromDraw(player.getDraw().size()));
				game.message(player, "putting your deck into your discard pile immediately");
				game.messageOpponents(player, "putting his deck into his discard pile immediately");
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
