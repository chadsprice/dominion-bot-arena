package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Chapel extends Card {

	public Chapel() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// trash up to 4 cards from hand
		List<Card> trashed = game.promptTrashNumber(player, 4, false, "Chapel");
		player.removeFromHand(trashed);
		game.addToTrash(trashed);
		game.messageAll("trashing " + Card.htmlList(trashed));
	}

	@Override
	public String[] description() {
		return new String[]{"Trash up to 4 cards from your hand."};
	}
	
	@Override
	public String toString() {
		return "Chapel";
	}

}
