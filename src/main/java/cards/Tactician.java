package cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Tactician extends Card {

	@Override
	public String name() {
		return "Tactician";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.DURATION);
	}

	@Override
	public int cost() {
		return 5;
	}

    @Override
    public String[] description() {
        return new String[] {
                "Discard your hand.",
                "If you discarded any_cards this_way, then at the_start of your next_turn, <+5_Cards>, <+1_Buy>, and <+1_Action>."
        };
    }

	@Override
	public boolean onDurationPlay(Player player, Game game, List<Card> havenedCards) {
		if (!player.getHand().isEmpty()) {
			List<Card> allCardsInHand = new ArrayList<>(player.getHand());
			player.putFromHandIntoDiscard(allCardsInHand);
			game.messageAll("discarding " + Card.htmlList(allCardsInHand));
			return true;
		} else {
			game.message(player, "but your hand is empty");
			game.messageOpponents(player, "but their hand is empty");
			return false;
		}
	}

	@Override
	public void onDurationEffect(Player player, Game game) {
		plusCards(player, game, 5);
		plusBuys(player, game, 1);
		plusActions(player, game, 1);
	}

}
