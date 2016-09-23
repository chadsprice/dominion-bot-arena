package cards;

import server.Card;
import server.Game;
import server.Player;

public class Watchtower extends Card {

	public Watchtower() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		if (player.getHand().size() < 6) {
			plusCards(player, game, 6 - player.getHand().size());
		}
	}

	@Override
	public String htmlClass() {
		return "reaction";
	}

	@Override
	public String htmlType() {
		return "Action-Reaction";
	}

	@Override
	public String[] description() {
		return new String[] {"Draw until you have 6 cards in hand.", "When you gain a card, you may reveal this from your hand. If you do, either trash that card or put it on top of your deck."};
	}

	@Override
	public String toString() {
		return "Watchtower";
	}

}
