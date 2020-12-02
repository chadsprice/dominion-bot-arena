package cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import server.*;

public class Minion extends Card {

	@Override
	public String name() {
		return "Minion";
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
				"<+1_Action>",
				"Choose_one:",
				"* <+2$>",
				"* Discard your hand, <+4_Cards>, and each_player with at_least 5_cards in_hand discards their_hand and draws_4_cards."
		};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusActions(player, game, 1);
		// choose +$2 or attack
		if (chooseCoinOverAttack(player, game)) {
			plusCoins(player, game, 2);
		} else {
			// discard hand and draw 4 cards
			game.messageAll("discarding " + Card.htmlList(player.getHand()));
			player.putFromHandIntoDiscard(new ArrayList<>(player.getHand()));
			plusCards(player, game, 4);
			// each other player with at least 5 cards discard their hand and draws 4 cards
			targets.forEach(target -> {
				if (target.getHand().size() >= 5) {
					game.message(target, "You discard " + Card.htmlList(target.getHand()));
					game.messageOpponents(target, target.username + " discards " + Card.htmlList(target.getHand()));
					target.putFromHandIntoDiscard(new ArrayList<>(target.getHand()));
					game.messageIndent++;
					plusCards(target, game, 4);
					game.messageIndent--;
				}
			});
		}
	}

	private boolean chooseCoinOverAttack(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).minionCoinOverAttack();
		}
		int choice = new Prompt(player, game)
                .message(this.toString() + ": Choose one")
                .multipleChoices(new String[] {"+$2", "Attack"})
                .responseMultipleChoiceIndex();
		return (choice == 0);
	}

}
