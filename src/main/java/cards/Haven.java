package cards;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.*;

public class Haven extends Card {

	@Override
	public String name() {
		return "Haven";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.DURATION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Card>",
				"<+1_Action>",
				"Set aside a_card from your_hand face_down. At the_start of your next_turn, put_it into_your_hand."
		};
	}

	@Override
	public boolean onDurationPlay(Player player, Game game, List<Card> havened) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		// choose a card to haven
		if (!player.getHand().isEmpty()) {
			Card toHaven = chooseSetAside(player, game);
			game.message(player, "setting aside " + toHaven.htmlName() + " face down");
			game.messageOpponents(player, "setting aside a card face down");
			player.removeFromHand(toHaven);
			havened.add(toHaven);
			// indicate that this haven will have an effect next turn
			return true;
		} else {
			game.message(player, "setting aside nothing because your hand is empty");
			game.messageOpponents(player, "setting aside nothing because their hand is empty");
			// indicate that this haven will have no effect next turn
			return false;
		}
	}

	private Card chooseSetAside(Player player, Game game) {
		Set<Card> canSetAside = new HashSet<>(player.getHand());
		if (player instanceof Bot) {
			Card toSetAside = ((Bot) player).havenSetAside(Collections.unmodifiableSet(canSetAside));
			checkContains(canSetAside, toSetAside);
			return toSetAside;
		}
		return new Prompt(player, game)
                .message(this.toString() + ": Choose a card to set aside.")
                .handChoices(canSetAside)
                .responseCard();
	}

	@Override
	public void onDurationEffect(Player player, Game game, DurationEffect duration) {
		game.message(player, "returning " + Card.htmlList(duration.havenedCards) + " to your hand");
		game.messageOpponents(player, "returning " + Card.numCards(duration.havenedCards.size()) + " to their hand");
		duration.havenedCards.forEach(player::removeDurationSetAside);
		player.addToHand(duration.havenedCards);
	}

}
