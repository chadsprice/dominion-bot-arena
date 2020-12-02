package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

public class GhostShip extends Card {

	@Override
	public String name() {
		return "Ghost Ship";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2_Cards>",
				"Each other player with 4_or_more cards in_hand puts_cards from their_hand on_top_of their_deck until they_have 3_cards in_their_hand."
		};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 2);
		// targets discard down to 3, putting the discarded cards on top of their decks
		targets.forEach(target -> {
			if (target.getHand().size() > 3) {
				int count = target.getHand().size() - 3;
				List<Card> toPutOnDeck = promptPutNumberOnDeck(
						target,
                        game,
                        count
                );
				target.removeFromHand(toPutOnDeck);
				target.putOnDraw(toPutOnDeck);
				game.message(target, "You put " + Card.numCards(toPutOnDeck.size()) + " on top of your deck");
				game.messageOpponents(target, target.username + " puts " + Card.numCards(toPutOnDeck.size()) + " on top of their deck");
			}
		});
	}

}
