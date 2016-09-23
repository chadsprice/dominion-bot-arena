package cards;

import server.Card;
import server.Game;
import server.Player;

public class WorkersVillage extends Card {

	public WorkersVillage() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 1);
		plusActions(player, game, 2);
		plusBuys(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+2 Actions", "+1 Buy"};
	}

	@Override
	public String toString() {
		return "Worker's Village";
	}

}
