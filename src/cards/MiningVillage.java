package cards;

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
		plusCards(player, game, 1);
		plusActions(player, game, 2);
		if (!hasMoved) {
			// you may trash this card for +$2
			int choice = game.promptMultipleChoice(player, "Mining Village: Trash the " + this.htmlName() + " for +$2?", new String[] {"Trash", "Keep"});
			if (choice == 0) {
				player.removeFromPlay(this);
				game.trash(player, this);
				player.addCoins(2);
				game.messageAll("trashing the " + this.htmlNameRaw() + " for +$2");
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+2 Actions", "You may trash this for +$2."};
	}

	@Override
	public String toString() {
		return "Mining Village";
	}

}
