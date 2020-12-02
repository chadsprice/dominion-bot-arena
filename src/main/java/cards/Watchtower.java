package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Watchtower extends Card {

	@Override
	public String name() {
		return "Watchtower";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public String htmlType() {
		return "Action-Reaction";
	}

	@Override
	public String htmlHighlightType() {
		return "reaction";
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[] {
				"Draw until you have 6_cards in_hand.",
				"When you gain a_card, you_may reveal_this from your_hand. If_you_do, either_trash that_card or put_it on_top of your_deck."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		if (player.getHand().size() < 6) {
			plusCards(player, game, 6 - player.getHand().size());
		}
	}

}
