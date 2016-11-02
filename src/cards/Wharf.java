package cards;

import server.Card;
import server.Game;
import server.Player;

public class Wharf extends Card {

	public Wharf() {
		isAction = true;
		isDuration = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
		plusBuys(player, game, 1);
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		plusCards(player, game, 2);
		plusBuys(player, game, 1);
	}

	@Override
	public String[] description() {
		return new String[] {"Now and at the start of your next turn:", "+2 Cards", "+1 Buy"};
	}

	@Override
	public String toString() {
		return "Wharf";
	}

}
