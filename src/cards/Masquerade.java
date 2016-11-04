package cards;

import server.Card;
import server.Game;
import server.Player;

public class Masquerade extends Card {

	public Masquerade() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
        onMasqueradeVariant(player, game, false);
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Each player with any cards in hand passes one to the next such player to their left, at once. Then you may trash a card from your hand."};
	}

	@Override
	public String toString() {
		return "Masquerade";
	}

}
