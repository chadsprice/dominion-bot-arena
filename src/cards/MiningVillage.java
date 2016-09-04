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
	public void onPlay(Player player, Game game) {
		// +1 card
		List<Card> drawn = player.drawIntoHand(1);
		// +2 actions
		player.addActions(2);
		game.message(player, "... You draw " + Card.htmlList(drawn) + " and get +2 actions");
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s) and getting +2 actions");
		// you may trash this card for +$2
		int choice = game.promptMultipleChoice(player, "Mining Village: Trash the " + this.htmlName() + " for +$2?", new String[] {"Trash", "Keep"});
		if (choice == 0) {
			player.removeFromPlay(this);
			game.trash.add(this);
			player.addExtraCoins(2);
			game.message(player, "... You trash the " + this.htmlNameRaw() + " and get +$2");
			game.messageOpponents(player, "... trashing the " + this.htmlNameRaw() + " and getting +$2");
		}
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
