package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class MiningVillage extends Card {

	public MiningVillage() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		game.message(player, "drawing " + Card.htmlList(drawn));
		game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
		// +2 actions
		player.addActions(2);
		game.messageAll("getting +2 actions");
		if (!hasMoved) {
			// you may trash this card for +$2
			int choice = game.promptMultipleChoice(player, "Mining Village: Trash the " + this.htmlName() + " for +$2?", new String[] {"Trash", "Keep"});
			if (choice == 0) {
				player.removeFromPlay(this);
				game.trash.add(this);
				player.addExtraCoins(2);
				game.messageAll("trashing the " + this.htmlNameRaw() + " for +$2");
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+2 Actions", "You may trash this card immediately.", "If you do, +$2."};
	}

	@Override
	public String toString() {
		return "Mining Village";
	}

}
