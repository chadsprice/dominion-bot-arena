package cards;

import server.Bot;
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
		if (!player.getDraw().isEmpty() && choosePutDeckIntoDiscard(player, game)) {
            game.message(player, "putting your deck into your discard pile immediately");
            game.messageOpponents(player, "putting their deck into their discard pile immediately");
            // this does not trigger the Tunnel reaction
            player.addToDiscard(player.takeFromDraw(player.getDraw().size()), false);
		}
	}

	private boolean choosePutDeckIntoDiscard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).chancellorPutDeckIntoDiscard();
        }
        int choice = game.promptMultipleChoice(player, this.toString() + ": Put your deck into your discard pile immediately?", new String[]{"Yes", "No"});
        return (choice == 0);
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
